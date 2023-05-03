module.exports = {
  title: 'FEEL-Scala',
  tagline: 'A FEEL engine written in Scala, by Camunda.',
  url: 'https://camunda.github.io',
  baseUrl: '/feel-scala/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.png',
  organizationName: 'camunda',
  projectName: 'feel-scala',
  themeConfig: {
    navbar: {
      title: 'FEEL-Scala',
      logo: {
        alt: 'Camunda Logo',
        src: 'img/camunda-logo.png',
      },
      items: [
        {
          // type: 'doc',
          // docId: 'introduction/what-is-feel',
          to: 'docs/reference/',
          activeBasePath: 'docs/docs',
          label: 'Reference',
          position: 'left',
        },
        {
          // type: 'doc',
          // docId: 'samples/samples',
          to: "docs/learn/",
          activeBasePath: "docs/learn",
          label: "Learn",
          position: "left",
        },
        {
          to: "docs/playground/",
          activeBasePath: "docs/playground",
          label: "Playground",
          position: "left",
        },
        {
          to: "docs/changelog/",
          activeBasePath: "docs/changelog",
          label: "Changelog",
          position: "left",
        },
        {
          type: 'docsVersionDropdown',
          position: 'right',
          //dropdownActiveClassDisabled: true
        },
        {
          href: 'https://github.com/camunda/feel-scala',
          position: 'right',
          className: 'header-github-link'
        },
        {
          href: "https://forum.camunda.io/",
          position: 'right',
          className: 'header-forum-link'
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [ ],
      copyright: `Copyright © ${new Date().getFullYear()} Camunda`,
    },
    algolia: {
      appId: "3CFTU9C6BV",
      apiKey: "d99776b3dfcf4aa34670df2c65f266a3",
      indexName: "feel-scala",
      searchParameters: {}, // Optional (if provided by Algolia)
      contextualSearch: true,
    },
    docs: {
      sidebar: {
        hideable: true
      }
    },
    // syntax highlighter
    prism: {
      additionalLanguages: ['java', 'scala'],
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl:
            'https://github.com/camunda/feel-scala/edit/main/docs/',
          // includes the unreleased version
          includeCurrentVersion: true,
          // the last released (stable) version
          lastVersion: '1.16',
          // override the config for specific versions
          versions: {
            // for the unreleased version
            current: {
              // add the postfix "unreleased"
              label: '1.17 (unreleased)'
            },
            // for all supported versions
            '1.15': {
              // disable the "unmaintained version" banner
              banner: 'none',
            },
          },
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/camunda/feel-scala/edit/main/docs/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
