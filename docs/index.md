---
layout: page
title: Usage
menus:
    -
        title: Serialization
---

This short tutorial shows how to use a JsResource to serialize, deserialize EMF models in JSON format. This
requires that you are already familiar with the concept of Resource in EMF. Follow this tutorial if you are not
familiar to [EMF](http://www.vogella.de/articles/EclipseEMF/article.html).

## Serialization

To serialize objects in json using emfjson, simply register the json extension to the resource factroy registry, as follow:

{% highlight java %}
ResourceSet resourceSet = new ResourceSetImpl();
Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("json", new JsResourceFactoryImpl());
{% endhighlight %}

Then create an object and add it to a resource. Give the resource a URI that will be path to the resulting json file.

{% highlight java %}
Resource resource = resourceSet.createResource(URI.createURI("model.json"));
Map<String, Object> options = new HashMap<String, Object>();
options.put(EMFJs.OPTION_INDENT_OUTPUT, true);
options.put(EMFJs.OPTION_SERIALIZE_TYPE, false);

LibraryShelf aShelf = LibraryFactory.eINSTANCE.createLibraryShelf();
aShelf.setName("My Shelf");

Book aBook = LibraryFactory.eINSTANCE.createBook();
aBook.setTitle("EMF: Eclipse Modeling Framework (2nd Edition)");
aBook.setNumPages(739);
aShelf.getItems().add(aBook);

resource.getContents().add(aShelf);
resource.save(options);
{% endhighlight %}

The resulting file ```model.json``` will then contain the following JSON object:

{% highlight javascript %}
{
  "name" : "My Shelf",
  "items" : [ {
    "title" : "EMF: Eclipse Modeling Framework (2nd Edition)",
    "numPages" : 739
  } ]
}
{% endhighlight %}

Setting the option ```OPTION_SERIALIZE_TYPE``` to true will result in the following JSON:

{% highlight javascript %}
{
  "eClass" : "http://www.eclipselabs.org/examples/library#//LibraryShelf",
  "name" : "My Shelf",
  "items" : [ {
    "eClass" : "http://www.eclipselabs.org/examples/library#//Book",
    "title" : "EMF: Eclipse Modeling Framework (2nd Edition)",
    "numPages" : 739
  } ]
}
{% endhighlight %}

## Deserialization

To load objects from json, you also need to register the correct resource factory, as previously described. You also
need to indicate the type of the root objects you want to obtain from the json data. This is done via a Map.

{% highlight java %}
Map<String, Object> options = new HashMap<String, Object>();
options.put(EMFJs.OPTION_ROOT_ELEMENT, ModelPackage.eINSTANCE.getUser());
{% endhighlight %}

Then load the EMF resource with those options.

{% highlight java %}
Resource resource = resourceSet.createResource(uri("file:/in.json"));
resource.load(options);
{% endhighlight %}

The resource should now contain the objects.

{% highlight java %}
User user = (User)resource.getContents().get(0);
{% endhighlight %}

