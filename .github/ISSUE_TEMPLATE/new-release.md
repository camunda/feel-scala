---
name: New Release
about: Building a new release
title: "[Release] 1.x.y"
labels: 'type: release'
assignees: koevskinikola, saig0

---

**Build a new Release**

Release version: 1.x.y
Release date: 

* [ ] inform the maintainers of other teams about the release
 * Camunda BPM: @koevskinikola / @ThorbenLindhauer
 * Zeebe: @saig0 / @npepinpe
* [ ] schedule a release date
* [ ] before building the release, inform the maintainers of other teams about the code freeze
* [ ] build the release using the CI job: https://ci.cambpm.camunda.cloud/view/Sideprojects/job/camunda-github-org/job/feel-scala/job/master
* [ ] deploy to Maven Central by releasing the staging repository: https://oss.sonatype.org/#stagingRepositories
* [ ] if major/minor release, archive the documentation of the previous version
  * copy `/docs/develop/` to `/docs/<PREVIOUS_VERSION>` 
  * add entry point for the version to `/docs/index.md`
* [ ] create a release in GitHub for the tag: https://github.com/camunda/feel-scala/releases
  * attach the artifacts from Nexus: https://app.camunda.com/nexus/#browse/search=keyword%3Dfeel-scala
  * write the changelog
* [ ] inform the maintainers of other teams about the successful release :tada:
