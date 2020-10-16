(ns todo-backend.handler.impl
  (:require
   [todo-backend.handler.protocol :as p]
   [todo-backend.store.protocol :as store]))

(defn ok [body]
  {:status 200
   :body body})

(defn- host
  [request]
  (-> request :headers (get "host" "localhost")))

(defn- scheme
  [request]
  (name (:scheme request)))

(defn append-todo-url [todo request]
  (let [host (host request)
        scheme (scheme request)
        id (:id todo)]
    (assoc todo :url (str scheme "://" host "/todos/" id))))

(defn create-handler
  [store]
  (reify p/Handler
    (get-all-todos [this req]
      (ok (mapv
           #(append-todo-url % req)
           (store/get-all-todos store))))
    (create-todos [this request]
      (->
       (store/create-todos store (-> request :body-params))
       (append-todo-url request)
       ok))
    (delete-all-todos [this]
      (store/delete-all-todos store)
      {:status 204})
    (get-todo [this request]
      (let [id (-> request :parameters :path :id)]
        (ok (append-todo-url (store/get-todo store id) request))))
    (update-todo [this request]
      (let [id (-> request :parameters :path :id)
            body (-> request :body-params)]
        (ok
         (append-todo-url
          (store/update-todo store id body)
          request))))
    (delete-todo [this request]
      (store/delete-todo store (-> request :parameters :path :id))
      {:status 204})))
