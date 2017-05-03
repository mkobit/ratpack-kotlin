package versioning.git

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

/**
 * This plugin is made so that revisions can be auto-tagged based on the project version being a
 * version line like {@code 0.2.x}, or {@code 1.0.x}.
 *
 * <p>This plugin achieves versioning by:
 * <ol>
 *   <li>Using the declared {@code project.version} as a version line</li>
 *   <li>Querying the Git tags of this repository according to that query</li>
 *   <li>Replacing the {@code project.version} with the calculated version</li>
 *   <li>Creating tasks to locally create and also push a Git tag</li>
 * </ol>
 * This happens before any other evaluation happens, so that the version will be represented in any
 * tasks that make use of it (for example, {@code groovydoc}, {@code jar}, and others).
 */
class VersioningPlugin implements Plugin<Project> {

  static final String VERSIONING_GROUP = 'Versioning'
  static final String TAG_NEXT_VERSION_TASK = 'tagNextVersion'
  static final String PUSH_TAG_VERSION_TASK = 'pushVersionTag'

  private static final Pattern VERSION_LINE_PATTERN = Pattern.compile('^(\\d+\\.\\d+\\.)[xX]$')

  private static final Pattern VERSION_PATTERN = Pattern.compile('^(\\d+\\.\\d+\\.)(\\d+)$')

  @Override
  void apply(final Project project) {
    if (project != project.rootProject) {
      throw new GradleException('Can only be applied to root project')
    }
    final logger = project.logger
    if (project.version == null) {
      throw new InvalidUserDataException('project.version must be specified')
    }

    // Project version is used as the version line - 1.0.x, 1.4.x, 0.1.x, etc.
    final versionFilter = project.version as String
    final Matcher versionFilterMatcher = VERSION_LINE_PATTERN.matcher(versionFilter)
    if (!versionFilterMatcher.matches()) {
      throw new InvalidUserDataException(
        "${versionFilter} is an invalid version filter - it must match $VERSION_LINE_PATTERN"
      )
    }

    logger.info('Performing versioning calculation for version filter {}', versionFilter)

    final Optional<TaggedVersion> lastVersion = getLastVersionForFilter(
      project,
      versionFilter,
      versionFilterMatcher
    )

    final TaggedVersion nextVersion = computeNextVersion(
      project,
      lastVersion,
      versionFilterMatcher
    )
    logger.lifecycle('Calculated next version {} for current revision {} from version filter {}',
                     nextVersion.tagName, nextVersion.revision, versionFilter)
    project.ext['revision'] = nextVersion.revision

    // We need to check if the previously tagged revision is the same as this revision so that
    // we do not accidentally try to re-tag it.
    // The tagging task will only execute if the revision has changed.
    final boolean isDifferentRevision = lastVersion.map {
      logger.info('Previous version {} for revision {}', it.tagName, it.revision)
      it.revision != nextVersion.revision
    }.orElse(true)

    if (isDifferentRevision) {
      project.logger.lifecycle('Using calculated next version {}', nextVersion.tagName)
      project.allprojects {
        version = nextVersion.tagName
      }
    } else {
      project.logger.lifecycle('Revisions are equivalent, reusing tag version {}',
                               lastVersion.get().tagName)
      project.allprojects {
        version = lastVersion.get().tagName
      }
    }

    final tagTask = project.tasks.create(TAG_NEXT_VERSION_TASK, Exec) {
      it.with {
        group = VERSIONING_GROUP
        description = 'Tags revision using the git CLI'
        inputs.property('revision', nextVersion.revision)
        inputs.property('tagName', project.version)
        onlyIf {
          isDifferentRevision
        }
        commandLine(
          'git',
          'tag',
          '--annotate',
          it.project.version,
          nextVersion.revision,
          '-m',
          'Automated Gradle Release'
        )
      }
    }
    project.tasks.create(PUSH_TAG_VERSION_TASK, Exec) {
      it.with {
        dependsOn(tagTask)
        group = VERSIONING_GROUP
        description = 'Pushes Git tag to remote'
        commandLine(
          'git',
          'push',
          'origin',
          "refs/tags/${project.version}",
          'HEAD:master'
        )
        onlyIf {
          // First check if remote tag exists
          final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
          project.exec {
            it.commandLine(
              'git',
              'ls-remote',
              'origin',
              "refs/tags/${project.version}",
            )
            it.standardOutput = outputStream
          }.assertNormalExitValue().rethrowFailure()
          final String lsOutput = outputStream.toString().trim()
          // If output is empty, means the remote tag does not exist yet
          return lsOutput.empty
        }
      }
    }
  }

  /**
   * Gets the latest version for the version filter and writes the JSON to the output file.
   */
  private static Optional<TaggedVersion> getLastVersionForFilter(
    final Project project,
    final String versionFilter,
    final Matcher versionFilterMatcher
  ) {
    // Replace each '.' with a '\.' for use with the 'git tag' CLI so that
    // it treats '.' as a literal period rather than a "match anything".
    // '*' is also appended for the matching pattern used by "git tag".
    final String gitTagFilter = versionFilterMatcher.group(1).replaceAll('\\.', '\\\\.') + '*'
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
    project.exec {
      // The *objectname gets the actual commit that the tag is pointing to rather than the
      // object of the tag itself.
      it.commandLine(
        'git',
        'tag',
        '--format=%(refname:strip=2),%(*objectname)',
        '--sort=-v:refname',
        '--list',
        gitTagFilter
      )
      it.standardOutput = outputStream
    }.assertNormalExitValue().rethrowFailure()

    final String outputString = outputStream.toString().trim()
    if (outputString.empty) {
      project.logger.lifecycle('No last version for filter {}', versionFilter)
      return Optional.empty()
    } else {
      // Each format string is listed on a new line, so split them up
      final String[] splitByLine = outputString.split(System.lineSeparator())
      final String latestInVersionLine = splitByLine.first()
      final String[] splitOutput = latestInVersionLine.split(',', 2)
      final TaggedVersion version = new TaggedVersion(splitOutput[0], splitOutput[1])
      project.logger.lifecycle('Last version for filter {} was {} for revision {}',
                               versionFilter, version.tagName, version.revision)
      return Optional.of(version)
    }
  }

  private static TaggedVersion computeNextVersion(
    final Project project,
    final Optional<TaggedVersion> lastVersion,
    final Matcher versionFilterMatcher
  ) {
    final String revision = project.findProperty('git.revision') ?:
      'git rev-parse HEAD'.execute().text.trim()
    return lastVersion.map {
      final mostRecentVersion = it.tagName
      final mostRecentVersionMatcher = VERSION_PATTERN.matcher(mostRecentVersion)
      if (!mostRecentVersionMatcher.matches()) {
        throw new GradleException("Most recent version $mostRecentVersion does not match pattern $VERSION_PATTERN")
      }
      // We have matched the pattern, so increment the last number to get the next version
      final majorMinor = mostRecentVersionMatcher.group(1)
      final patchAsNumber = Integer.parseInt(mostRecentVersionMatcher.group(2))
      final patch = (patchAsNumber + 1) as String

      return new TaggedVersion("${majorMinor}${patch}", revision)
    }.orElseGet {
      // No last version, so take the beginning of the filter for a new version line
      return new TaggedVersion(versionFilterMatcher.group(1) + '0', revision)
    }
  }

  /**
   * Simple value class representing a tag and revision.
   */
  @ToString(includePackage = false)
  @EqualsAndHashCode
  private static class TaggedVersion {
    final String tagName
    final String revision

    private TaggedVersion(final String tagName, final String revision) {
      this.tagName = tagName
      this.revision = revision
    }
  }
}
