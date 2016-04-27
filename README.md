# riemann-jmx-clj

A Clojure clone of riemann-jmx with states and systematic service name support which is forked from riemann-jmx repository.

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
