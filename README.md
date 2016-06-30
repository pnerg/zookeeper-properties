[![Build Status](https://travis-ci.org/pnerg/zookeeper-properties.svg?branch=master)](https://travis-ci.org/pnerg/zookeeper-properties) [![codecov.io](https://codecov.io/github/pnerg/zookeeper-properties/coverage.svg?branch=master)](https://codecov.io/github/pnerg/zookeeper-properties?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.dmonix.zookeeper/zookeeper-properties/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.dmonix.zookeeper/zookeeper-properties) [![Javadoc](http://javadoc-badge.appspot.com/org.dmonix.zookeeper/zookeeper-properties.svg?label=javadoc)](http://javadoc-badge.appspot.com/org.dmonix.zookeeper/zookeeper-properties) 
# ZooKeeper Properties
Utility for reading/storing properties (name/value pairs) from/to ZooKeeper

A trending architectural principle is the one of _micro services_, i.e. small self-contained application performing a very limited set of tasks. In an essence stand-alone processes performing some task. A typical problem in a micro services architecture is to configure each individual service. Not only are the services potentially distributed over a number of hosts, there's most likley going to be multiple instances of each service.  
The common pattern is to use a centralized database for storage, of which [ZooKeeper](https://zookeeper.apache.org/) is very popular.  

Properties tend to be something we use property files for and let the application read during startup.  
This approach becomes awfully cumbersome when faced with a multitude of instances on different hosts.  

This library provides a simple to use to read properties for an application from ZooKeeper.  

## Data model
The data model is simple and straightforward.  
There is support for multiple property sets, e.g. each application/service may have its own specific set of properties.  
Each property set is stored in its own path under which the name/value of each property is stored.  
The example below illustrates two separate property sets _global_ and _service-a_
```
[root-path]/properties
        /global
            /db.host[localhost]
            /db.port[6969]
        /service-a
            /max.threads[100]
```
## Code Examples
It all starts by creating a _PropertiesStorageFactory_ which is in an essence is the builder for creating instance of _PropertiesStorage_ instances.  
The factory itself is based on the _builder_ pattern allowing you to choose what properties to set.
```java
PropertiesStorageFactory factory = PropertiesStorageFactory.apply("localhost:6181")
		.withRootPath("/etc/data");
Try<PropertiesStorage> propertiesStorage = factory.create();
```
Now assuming we got a _Successful_ response containing a _PropertiesStorage_ instance we can use it to:
### Store property set
```java
PropertiesStorage propertiesStorage = ...
PropertySet ps = PropertySet.apply("example-app");
ps.set("db.host", "some-host");
ps.set("user.name", "Peter");
Try<Unit> result = propertiesStorage.store(ps);
```

### List property set names
```java
PropertiesStorage propertiesStorage = ...
Try<List<String>> sets = propertiesStorage.propertySets();
```
### Get properties for a set
```java
PropertiesStorage propertiesStorage = ...
Try<Option<PropertySet>> properties = propertiesStorage.get("example-app");
```

### Delete a property set
```java
PropertiesStorage propertiesStorage = ...
Try<Unit> result = propertiesStorage.delete("example-app");
```

## Management of properties
To further ease the management of the properties in ZooKeeper there is a companion project [RESTful ZooKeeper Properties](https://github.com/pnerg/restful-zookeeper-properties) which provides a RESTful interface to manage the data.  
Allowing for non-programmatic access using tools such as [wget](https://www.gnu.org/software/wget/) and [curl](http://man.cx/curl).

## References
This project builds heavily on both _Lambda_ operations as well as a more functional programming paradigm.  
Some of the types such as _Option_,_Try_ and _Future_ may seem a bit confusing at first.  
Refer to the project [java-scala-util](https://github.com/pnerg/java-scala-util) for details and examples on how to work with features such as [Option](https://github.com/pnerg/java-scala-util/wiki/Option)/[Try](https://github.com/pnerg/java-scala-util/wiki/Try)/[Future](https://github.com/pnerg/java-scala-util/wiki/Future)

## Related projects
There are a few companion projects that may be of use.

* [RESTful ZooKeeper Properties](https://github.com/pnerg/restful-zookeeper-properties)  
  Provides a RESTful interface to this project. I.e. the possibility to manage the data model over HTTP/REST.
* [ZooKeeper Unit](https://github.com/pnerg/zookeeper-junit)  
Utility for managing a ZooKeeper server during JUnit testing.

## LICENSE
Copyright 2016 Peter Nerg.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
