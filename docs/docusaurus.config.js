module.exports = {
  title: 'FEEL-Scala',
  tagline: 'A FEEL parser and interpreter written in Scala.',
  url: 'https://camunda.github.io',
  baseUrl: '/feel-scala/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
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
          to: "docs/samples/",
          activeBasePath: "docs/samples",
          label: "Samples",
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
          href: "https://forum.camunda.org/",
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
      apiKey: "x",
      indexName: "feel-scala",
      searchParameters: {}, // Optional (if provided by Algolia)
      contextualSearch: true,
    },
    hideableSidebar: true,
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl:
            'https://github.com/camunda/feel-scala/edit/master/docs/',
          lastVersion: 'current',
          // onlyIncludeVersions: ['current', '1.12', '1.11'],
          versions: {
            current: {
              label: `1.13 (unreleased)`,
            },
          },
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/camunda/feel-scala/edit/master/docs/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
