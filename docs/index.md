---
layout: page
title: Documentation
---

# Usage

In this section, we will introducd the basic usage of EMFJson with the EMF API and with the Jackson API and on GWT. EMFJson is built on top of the Jackson JSON library and can be used as a Jackson module, but can also be easily used from the EMF API. The GWT version of EMFJson does not make use of the Jackson library, as it is intented to be compiled as JavaScript, and be used in complement to GWT version of EMF.

## EMF

```java
Resource ....
```

## Jackson


```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new EMFModule());
```

### Extensions


## GWT


# JSON Format

EMFJson format aims to simplify the definition of EMF models by using a more human readable format than the default XML format prodived by the EMF framework. It also simplifies the use of EMF data models in modern development stack that invlove different technologies and make heavy use of JSON, for example when developing Web applications.

EMFJson format is simple and customizable and preserves the specificities of EMF, e.g. object fragment identifiers, inter and cross documents references.

## JSON Document

A JSON document contains the description of a model. It can contain a single element as a JSON Object or a collection of elements as a JSON Array. 

Below a document containing a single element. The element is an object of type `EClass` and contains 2 features, being respectively an `EAttribute` and a `EReference`.

```json
{
    "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EClass",
    "name": "Foo",
    "eStructuralFeatures": [
        {
            "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EAttribute",
            "name": "bar",
            "eType": "http://www.eclipse.org/emf/2002/Ecore#//EString"
        },
        {
            "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EReference",
            "name": "foos",
            "upperBound": -1,
            "eType": "http://www.eclipse.org/emf/2002/Ecore#//Foo"
        }
    ]
}
```

The second document contains a collection of objects. Those 2 objects are of type EClass.

```json
[
    {
        "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EClass",
        "name": "A"        
    },
    {
        "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EClass",
        "name": "B",
        "eSuperTypes": [ "model.ecore#//A" ]
    }
]
```

We can note that in this document the second object declares an inter-reference to the first object via it's property `eSuperTypes`.

Internal and external or cross references represent link between objects. In EMF, those links are defined by `EReferences` that are generally not containments. 

An example of external references can be found in both example documents under the objects' property `eClass`. The property declares the type of the object, that is another object store in a document identified by a URI.

We will come back later on the format used to represent objects' references inside a same document and between objects from different documents.

## EObject

EObjects are represented in JSON as JSON object (like this `{}`).

Each key of the JSON object represents a structural feature (`EAttribute` or `EReference`) of the EObject. The associated value is the value of the structural feature. The value can be represented in the form of a `string`, `number`, `boolean`, `object` or `array` depending on the type of the feature.

EMFJson format uses a special key to identify the type of an object. This special key is `eClass` and gives us the type of the object in the form of a URI. 

The following example presents the representation of an instance of EClass as JSON object.

```json
{
	"eClass": "http://www.eclipse.org/emf/2002/Ecore#//EPackage",
	"name": "myModel",
	"nsURI": "http://example.org/myModel",
	"eClassifiers": [
		{
    		"eClass": "http://www.eclipse.org/emf/2002/Ecore#//EClass",
		    "name": "Foo"
	    }
	]
}
```

```json
{
    "eClass": "http://example.org/myModel#//Foo"
}
```

## EList

EList are represented in the form of JSON arrays. Each element of the array is a JSON object.

```json
{
    "eClass": "http://www.eclipse.org/emf/2002/Ecore#//EClass",
    "eStructuralFeatures": [
        { ... },
        { ... }
    ]
}
```

## EAttributes

EAttributes are properties of EObjects and are mapped to JSON key values, where values are primitive types (string, number, boolean).

```json
{
    "name": "Joe",
    "age": 18,
    "male": true
}
```

## EReferences

EReferences represent links between EObjects. They can be containments or references. In both cases, they can link elements
from the same document or link elements from different documents.

### Containments

Single value containment:

```json
{
    "eClass": "http://www.eclipse.org/emf/2002/Ecore#//Foo",
    "element": {
        "eClass": "http://www.eclipse.org/emf/2002/Ecore#//Bar"
    }
}
```

Multi value containment:

```json
{
    "eClass": "http://www.eclipse.org/emf/2002/Ecore#//Foo",
    "elements": [
        {
            "eClass": "http://www.eclipse.org/emf/2002/Ecore#//Bar"
        },
        {
            "eClass": "http://www.eclipse.org/emf/2002/Ecore#//Bar"
        }
    ]
}
```

### Inner References


References are represented as a JSON object containing a key ```$ref```. In the case of an inner document reference, the value of
the key is the fragment identifier of the referenced object.

Single value reference:

```json
{
    "eClass": "http://www.eclipselabs.org/emfjson/junit#//Node",
    "label": "root",
    "target": {
        "$ref": "//@child.0"
    },
    "child": [
        {
            "eClass": "http://www.eclipselabs.org/emfjson/junit#//Node",
            "label": "n1",
            "source" : {
                "$ref": "/"
            }
        }
    ]
}
```

Multi value references are represented by JSON object in an array:

```json
{
    "eClass": "...",
    "element": [
        {
            "$ref": "//@foo.0/@foo.1"
        },
        {
            "$ref": "//@foo.0/@foo.2"
        }
    ]
}
```

### Cross References

Single value reference:

```json
{
    "userId": "1",
    "name": "Paul",
    "friends": [
        {
            "$ref" : "platform:/plugin/org.eclipselabs.emfjson.junit/tests/test-proxy-2.json#2"
        } 
    ],
    "uniqueFriend": {
        "$ref" : "platform:/plugin/org.eclipselabs.emfjson.junit/tests/test-proxy-2.json#3"
    }
}
```

