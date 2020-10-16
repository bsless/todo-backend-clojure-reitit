(ns todo-backend.ring
  (:require
   [muuntaja.core :as m]
   [reitit.coercion.malli]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [ring.middleware.cors :refer [wrap-cors]]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [todo-backend.handler.protocol :as handler]))

(defn app
  [handler]
  (ring/ring-handler
   (ring/router
    [["/todos" {:get (fn [req] (handler/get-all-todos handler req))
                :post (fn [req] (handler/create-todos handler req))
                :delete (fn [_] (handler/delete-all-todos handler))
                :options (fn [_] {:status 200})}]
     ["/todos/:id" {:parameters {:path [:map [:id int?]]}
                    :get (fn [req] (handler/get-todo handler req))
                    :patch (fn [req] (handler/update-todo handler req))
                    :delete (fn [req] (handler/delete-todo handler req))}]]
    {:data {:muuntaja m/instance
            :coercion reitit.coercion.malli/coercion
            :middleware [muuntaja/format-middleware
                         rrc/coerce-response-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-exceptions-middleware
                         [wrap-cors :access-control-allow-origin [#".*"]
                          :access-control-allow-methods [:get :put :post :patch :delete]]]}})
   (ring/create-default-handler
    {:not-found (constantly {:status 404 :body "Not found"})})))
