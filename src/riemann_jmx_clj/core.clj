(ns riemann-jmx-clj.core
  (:require [clojure.java.jmx :as jmx]
            [clj-yaml.core :as yaml]
            [riemann.client :as riemann]
            [clojure.pprint :refer (pprint)])
  (:gen-class)
  (:import (java.net InetAddress)))

(defn- get-riemann-connection-helper
  [host port]
  (def clnt (riemann/tcp-client :host host :port port))
    (riemann/connect-client clnt))

(let [get-riemann-connection-helper (memoize get-riemann-connection-helper)]
  (defn get-riemann-connection
    ([host]
     (get-riemann-connection-helper host 5555))
    ([host port]
     (get-riemann-connection-helper host port))))

(defn run-queries
  "Takes a parsed yaml config and runs the contained queries, returning a seq of Riemann events."
  [yaml]
  (let [{:keys [jmx queries]} yaml]
    (->> (jmx/with-connection jmx
           (doall
             (for [{:keys [obj attr tags service description ttl]} queries
                   name (jmx/mbean-names obj)
                   attr attr]
               {:service (if service
                            (str service \. (first attr))
                            (str (.getCanonicalName ^javax.management.ObjectName name) \. attr))
                :host (if (:event_host jmx)
                        (:event_host jmx)
                        (or (:host jmx) (.getCanonicalHostName (InetAddress/getLocalHost))))
                :state (cond
                      (or (<= (jmx/read name (first attr)) (nth (nth attr 2) 0)) (>= (jmx/read name (first attr)) (nth (nth attr 2) 1))) "critical"
                      (or (<= (jmx/read name (first attr)) (nth (nth attr 1) 0)) (>= (jmx/read name (first attr)) (nth (nth attr 1) 1))) "warning"
                      :else "ok"
                        )
                :metric (jmx/read name (first attr))
                :description description
		            :ttl ttl
                :tags tags})))
         (mapcat (fn [{:keys [service metric] :as event}]
                   (if (map? metric)
                     (for [[k v] metric]
                       (assoc event
                              :service (str service \.(name k))
                              :metric v))
                     [event]))))))

(defn run-configuration
  "Takes a parsed yaml config, runs the queries, and posts the results to riemann"
  [yaml]
  (let [{{:keys [host port]} :riemann} yaml
        events (run-queries yaml)]
    (if port
    (get-riemann-connection host port)
    (get-riemann-connection host))
    (print ".")
    (flush)
    (riemann/send-events clnt events)))

(defn munge-credentials
  "Takes a parsed yaml config and, if it has jmx username & password,
   configures the jmx environment map properly. If only a username or
   password is set, exits with an error"
  [config]
  (let [{:keys [host port username password]} (:jmx config)]
    (when (and username (not password))
      (println "Provided username but no password.")
      (System/exit 1))
    (when (and password (not username))
      (println "Provided password but no username")
      (System/exit 1))
    (if (or username password)
      (assoc config :jmx {:host host :port port :environment {"jmx.remote.credentials" (into-array String [username password])}})
      config)))

(defn start-config
  "Takes a path to a yaml config, parses it, and runs it in a loop"
  [config]
  (let [yaml (yaml/parse-string (slurp config))
        munged (munge-credentials yaml)]
    (pprint munged)
    (future
        (try
          (run-configuration munged)
          (catch Exception e
            (.printStackTrace e)))
          (riemann/close-client clnt)
          (System/exit 0))))

(defn -main
  [& args]
  (doseq [arg args]
    (start-config arg)
    (println "Started monitors")))
