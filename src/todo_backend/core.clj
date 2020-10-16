(ns todo-backend.core
  (:require
   [todo-backend.store.impl :as store]
   [todo-backend.handler.impl :as handler]
   [todo-backend.ring :as ring]
   [todo-backend.server :as server]))

(defn -main [port]
  (let [url (store/jdbc-url)
        ds (store/datasource url)
        store (store/create-store ds)
        handler (handler/create-handler store)
        app (ring/app handler)]
    (server/start-httpkit app {:port (new Integer port) :join? false})))

(comment
  (require '[org.httpkit.client :as client]
           '[jsonista.core])
  (def url "jdbc:postgresql://localhost/todos?user=postgres&password=mypass")
  (def ds (store/datasource url))
  (def store (store/create-store ds))
  (def handler (handler/create-handler store))
  (def app (ring/app handler))
  (def server (server/start-httpkit app {:port 3000 :join? false}))
  (deref (client/request {:url "http://localhost:3000/todos" :method :get}))
  (def resp
    (deref
     (client/request
      {:url "http://localhost:3000/todos"
       :method :post
       :headers {"Content-type" "application/json"}
       :body (jsonista.core/write-value-as-bytes {:title "make dinner"})})))
  (def resp
    (deref
     (client/request
      {:url "http://localhost:3000/todos/5"
       :method :get
       :headers {"Content-type" "application/json"}})))
  (deref (client/request {:url "http://localhost:3000/todos" :method :delete}))
  (server :timeout 1000))
