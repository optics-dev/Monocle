const repoUrl = "https://github.com/optics-dev/monocle";

const apiUrl = "/Monocle/api/monocle/index.html";

// See https://docusaurus.io/docs/site-config for available options.
let siteConfig;
siteConfig = {
    title: "Monocle",
    tagline: "Optics for Scala",
    url: "https://optics.dev/Monocle",
    baseUrl: "/Monocle/",
    customDocsPath: "docs/target/mdoc",

    projectName: "monocle",
    organizationName: "optics-dev",

    headerLinks: [
        {href: apiUrl, label: "API Docs"},
        // { doc: "example", label: "Example" },
        {doc: "optics", label: "Optics"},
        {doc: "release_notes", label: "Release Notes"},
        {doc: "faq", label: "FAQ"},
        {href: repoUrl, label: "GitHub"}
    ],

    headerIcon: "img/monocle-full-white.png",
    titleIcon: "img/monocle-full.png",
    // titleIcon: "img/monocle-full.svg",
    favicon: "img/favicon.png",

    colors: {
        primaryColor: "#004A87",
        secondaryColor: "#0085E6"
    },

    copyright: `Copyright the maintainers © 2016-${new Date().getFullYear()}.`,

    highlight: {theme: "github"},

    onPageNav: "separate",

    separateCss: ["api"],

    cleanUrl: true,

    repoUrl,

    apiUrl
};

module.exports = siteConfig;
