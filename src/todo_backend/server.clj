(ns todo-backend.server
  (:require
   [ring.adapter.jetty :as jetty]
   [org.httpkit.server :as kit]))

(defn start-jetty
  [routes opts]
  (jetty/run-jetty routes opts))

(defn start-httpkit
  [routes opts]
  (kit/run-server routes opts))
