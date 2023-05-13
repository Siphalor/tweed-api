<div align="center">
    <img src="logo.png?raw=true" alt="Logo" width="150" height="150" />

# Tweed 4

![supported Minecraft versions: 1.14 | 1.15 | 1.16 | 1.17 | 1.18 | 1.19 | 1.20](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20-%2356AD56?style=for-the-badge)


[![latest maven release](https://img.shields.io/maven-metadata/v?color=0f9fbc&metadataUrl=https%3A%2F%2Fmaven.siphalor.de%2Fde%2Fsiphalor%2Ftweed4%2Ftweed4-bom-1.14%2Fmaven-metadata.xml&style=flat-square)](https://maven.siphalor.de/de/siphalor/tweed4/)

Yet another config API.

</div>

## Usage

To get the dependencies working, you may use the following setup:
```groovy
// build.gradle

repositories {
	maven { url 'https://maven.siphalor.de/' }
}

dependencies {
	modApi(platform("de.siphalor.tweed4:tweed4-bom-$minecraft_major_version:$tweed_version"))
	// Pick any modules you want to use, e.g.:
	include(modApi("de.siphalor.tweed4:tweed4-base-$minecraft_major_version"))
	include(modApi("de.siphalor.tweed4:tweed4-annotated-$minecraft_major_version"))
	include(modApi("de.siphalor.tweed4:tweed4-data-$minecraft_major_version"))
	include(modApi("de.siphalor.tweed4:tweed4-data-hjson-$minecraft_major_version"))
}
```

Use can find the latest version in the badge at the top of this README.
```properties
# gradle.properties

tweed_version=<latest version from badge>
minecraft_major_version=1.14
```

For a quick code example see [here](tweed-testmod/src/main/java/de/siphalor/tweedtest/Config.java).

## License

This mod is available under the Apache 2.0 License. Terms and conditions apply.
