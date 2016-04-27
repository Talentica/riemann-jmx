
# riemann-jmx-clj (Hopper)

Clojure client to fetch jmx events and pass it to riemann. It is based on riemann-jmx java library

## Building

Use `lein uberjar` to build the standalone jar. You can [download leiningen here](http://leiningen.org).

## Usage

Pass each of the riemann-jmx-config.yaml as command line options, e.g.:

```
java -jar riemann-jmx-clj-standalone.jar kafka.yml
```

Note that passing multiple config files is the same as running a separate copy of riemann-jmx
for each config file, except that it uses fewer resources.

Supports composite mbeans as well, unlike the current riemann-jmx.

See the kafka.yml for an example of how to write a configuration file with [ok,warning,critical] support for riemann.

The even will be sent only once. A sample tomcat.yaml and kafka.yaml file has been attached. Also mapping can be provided between service name and Mbean object.


## License

Copyright © 2013-2015 Two Sigma
Copyright © 2016 Talentica

Distributed under the Eclipse Public License version 1.0
