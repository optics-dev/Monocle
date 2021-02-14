const React = require("react");

const CompLibrary = require("../../core/CompLibrary.js");

// const variables = require(process.cwd() + "/variables.js");

const MarkdownBlock = CompLibrary.MarkdownBlock;
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

class HomeSplash extends React.Component {
  render() {
    const { siteConfig, language = "" } = this.props;
    const { baseUrl, docsUrl } = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ""}`;
    const langPart = `${language ? `${language}/` : ""}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

    const SplashContainer = props => (
      <div className="homeContainer">
        <div className="homeSplashFade">
          <div className="wrapper homeWrapper">{props.children}</div>
        </div>
      </div>
    );

    const ProjectTitle = () => (
      <h2 className="projectTitle">
          <img className="projectTitleLogo" src={siteConfig.titleIcon} />
        <small>{siteConfig.tagline}</small>
      </h2>
    );

    const PromoSection = props => (
      <div className="section promoSection">
        <div className="promoRow">
          <div className="pluginRowBlock">{props.children}</div>
        </div>
      </div>
    );

    const Button = props => (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={props.href} target={props.target}>
          {props.children}
        </a>
      </div>
    );

    return (
      <SplashContainer>
        <div className="inner">
          <ProjectTitle siteConfig={siteConfig} />
          <PromoSection>
            <Button href={siteConfig.apiUrl}>API Docs</Button>
            <Button href={docUrl("modules", language)}>Documentation</Button>
            <Button href={siteConfig.repoUrl}>View on GitHub</Button>
          </PromoSection>
        </div>
      </SplashContainer>
    );
  }
}

class Index extends React.Component {
  render() {
    const { config: siteConfig, language = "" } = this.props;
    const { baseUrl, docsUrl } = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ""}`;
    const langPart = `${language ? `${language}/` : ""}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

    const organization = "com.github.julien-truffaut";
    const latestVersion = "3.0.0-M1"

    const latestVersionBadge = latestVersion
      .replace("-", "--")
      .replace("_", "__");

    const Block = props => (
      <Container
        padding={["bottom", "top"]}
        id={props.id}
        background={props.background}
      >
        <GridBlock
          align="center"
          contents={props.children}
          layout={props.layout}
        />
      </Container>
    );

    const index = `[![Join the chat at https://gitter.im/optics-dev/Monocle](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/optics-dev/Monocle?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.julien-truffaut/monocle_2.12.svg)](http://search.maven.org/#search|ga|1|com.github.julien-truffaut.monocle)

Monocle is a Scala library which offers a simple yet powerful API to access and transform immutable data.

\`\`\`scala
import monocle.syntax.all._

val user = User( "Anna", Address(12, "high street"))

user.focus(_.name).replace("Bob")
// res: User = User( "Bob", Address(12, "high street"))

user.focus(_.address.streetName).modify(_.toUpperCase)
// res: User = User( "Anna", Address(12, "HIGH STREET"))

user.focus(_.address.streetNumber).get
// res: Int = 12
\`\`\`

## Installation

Monocle is published for Scala **2.13** and **3.0.0-M3**. You can add it to your sbt build with:


\`\`\`scala
libraryDependencies ++= Seq(
 "${organization}" %% "monocle-core"  % "${latestVersion}",
 "${organization}" %% "monocle-macro" % "${latestVersion}", // Not required for Scala 3
)
\`\`\`

## Copyright and license

All code is available to you under the MIT license, available [here](http://opensource.org/licenses/mit-license.php).
The design is informed by many other projects, in particular Haskell [Lens](https://github.com/ekmett/lens).

Copyright the maintainers, 2016 - 2021.

`.trim();

    return (
      <div>
        <HomeSplash siteConfig={siteConfig} language={language} />
        <div className="mainContainer">
          <div className="index">
            <MarkdownBlock>{index}</MarkdownBlock>
          </div>
        </div>
      </div>
    );
  }
}

module.exports = Index;
