---
layout: down
title: Download
---

# Download instructions

Choose the version of emfjson that is right for your target platform.

For server side or desktop Java applications, you need the version of emfjson-jackson that is available in Maven 
Central.

Eclipse plugins and Eclipse RCP developers should use the version of emfjson-jackson available in our p2 repository. This is 
the same version as the one available in Maven Central, only the build is 
To use this repository we encourage the use of tycho, the maven plugin for Eclipse applications dependency management.

Browser support is available through emfjson-gwt, that should be use with the [EMF-GWT generator](https://wiki.eclipse.org/EMF/GWT).
For Javascript applications, take a look at [ecorejs](http://ghillairet.github.io/ecore.js/) that supports the emfjson format
and make it easy to develop EMF based Web applications.

# Download emfjson-jackson

emfjson-jackson is available both on [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22emfjson-jackson%22) and
on a [p2 repository](http://ghillairet.github.io/p2).

## Maven users

Add the following dependency to your project [POM](http://maven.apache.org/pom.html).

```xml
<dependency>
    <groupId>org.emfjson</groupId>
    <artifactId>emfjson-jackson</artifactId>
    <version>0.10.0</version>
</dependency>
```

## Non Maven users

For users who want to do not use Maven and want to download the jars, those are available on
[Maven Central](http://search.maven.org/#search|ga|1|%20a%3A%22emfjson-core%22%20OR%20a%3A%22emfjson-jackson%22). Click on
the jar links to download them.

## Eclipse users

You can install emfjson Jackson in your Eclipse via the following update site

[http://ghillairet.github.io/p2](http://ghillairet.github.io/p2)

# GWT

```xml
<dependency>
    <groupId>org.emfjson</groupId>
    <artifactId>emfjson-gwt</artifactId>
    <version>0.10.0</version>
</dependency>
```

# Adapters

## MongoDB

```xml
<dependency>
    <groupId>org.emfjson</groupId>
    <artifactId>emfjson-mongo</artifactId>
    <version>0.1.0</version>
</dependency>
```

## CouchDB

```xml
<dependency>
    <groupId>org.emfjson</groupId>
    <artifactId>emfjson-couchdb</artifactId>
    <version>0.1.0</version>
</dependency>
```

# ecore.js

## NPM

```
npm install ecore --save
```

## Bower

```
bower install ecore
```

