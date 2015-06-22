---
layout: doc
title: Getting Started
---

This guide will get you started with EMFJson.

The following requires a basic knowledge of the Eclipse Modeling Framework, ([EMF](http://www.eclipse.org/emf)) and its API. 
You can follow this excellent [tutorial](http://www.vogella.de/articles/EclipseEMF/article.html) to get started with EMF.

## Installation

EMFJson Jackson can be used in both standalone Java applications and OSGI or Eclipse based Plugins. In this 
tutorial we will create a Java project with Maven.

To get started, first create an empty Maven project and add the following dependency

```xml
<dependency>
    <groupId>org.emfjson</groupId>
    <artifactId>emfjson-jackson</artifactId>
    <version>0.10.0</version>
</dependency>
```

## Quick Start

Create a ResourceSet and add the JsonResourceFactory to the factory map.

```java
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;

...

ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new JsonResourceFactory());
```

As we are working in a standalone environment (i.e. outside OSGI), we need to register the EPackages we 
are going to use, here the EcorePackage.

```java
resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
```

We can now create an Ecore model using the EMF dynamic API.

```java
EPackage fooPackage = EcoreFactory.eINSTANCE.createEPackage();
fooPackage.setName("foo");
fooPackage.setNsPrefix("foo");
fooPackage.setNsURI("http://sample.org/foo");

EClass fooClass = EcoreFactory.eINSTANCE.createEClass();
fooClass.setName("Foo");

EAttribute labelAttr = EcoreFactory.eINSTANCE.createEAttribute();
labelAttr.setName("label");
labelAttr.setEType(EcorePackage.Literals.ESTRING);

fooClass.getEStructuralFeatures().add(labelAttr);
fooPackage.getEClassifiers().add(fooClass);
```

And add the fooPackage in a resource.


```java
Resource res = resourceSet.createResource(URI.createURI("foo.json"));
res.getContents().add(fooPackage);
```

We can now save the resource content as JSON like this:

```java
res.save(null);
```

That's it, you've learn how to save an EMF Resource in JSON with EMFJson. For more 
information about EMFJson features check the [documentation](/docs).
