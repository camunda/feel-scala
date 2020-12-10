/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
import React from "react";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import Link from "@docusaurus/Link";
import {
  useActivePlugin,
  useActiveVersion,
  useDocVersionSuggestions,
} from "@theme/hooks/useDocs";
import { useDocsPreferredVersion } from "@docusaurus/theme-common";

const getVersionMainDoc = (version) =>
    version.docs.find((doc) => doc.id === version.mainDocId);

function DocVersionSuggestions() {
  const {
    siteConfig: { title: siteTitle },
  } = useDocusaurusContext();
  const { pluginId } = useActivePlugin({
    failfast: true,
  });
  const { savePreferredVersionName } = useDocsPreferredVersion(pluginId);
  const activeVersion = useActiveVersion(pluginId);
  const {
    latestDocSuggestion,
    latestVersionSuggestion,
  } = useDocVersionSuggestions(pluginId); // No suggestion to be made

  if (!latestVersionSuggestion) {
    return <></>;
  } // try to link to same doc in latest version (not always possible)
  // fallback to main doc of latest version

  const latestVersionSuggestedDoc =
      latestDocSuggestion ?? getVersionMainDoc(latestVersionSuggestion);
  return <></>;
  // @saig0: removed old version banner, should be configurable in future
  // https://github.com/facebook/docusaurus/issues/3013
  // Command to separate components: https://v2.docusaurus.io/docs/cli/#docusaurus-swizzle
  // For this component: node_modules/@docusaurus/core/bin/docusaurus.js swizzle @docusaurus/theme-classic DocVersionSuggestions --danger
  //
  //   return (
  //     <div className="alert alert--warning margin-bottom--md" role="alert">
  //       {
  //         // TODO need refactoring
  //         activeVersion.name === 'current' ? (
  //           <div>
  //             This is unreleased documentation for {siteTitle}{' '}
  //             <strong>{activeVersion.label}</strong> version.
  //           </div>
  //         ) : (
  //           <div>
  //             This is documentation for {siteTitle}{' '}
  //             <strong>{activeVersion.label}</strong>, which is no longer actively
  //             maintained.
  //           </div>
  //         )
  //       }
  //       <div className="margin-top--md">
  //         For up-to-date documentation, see the{' '}
  //         <strong>
  //           <Link
  //             to={latestVersionSuggestedDoc.path}
  //             onClick={() =>
  //               savePreferredVersionName(latestVersionSuggestion.name)
  //             }>
  //             latest version
  //           </Link>
  //         </strong>{' '}
  //         ({latestVersionSuggestion.label}).
  //       </div>
  //     </div>
  //   );
}

export default DocVersionSuggestions;