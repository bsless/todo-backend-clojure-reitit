(ns todo-backend.store.impl
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [next.jdbc.sql :as sql]
   [todo-backend.store.protocol :as p]))

(defn- rename-key
  [m from to]
  (if-let [e (find m from)]
    (assoc (dissoc m from) to (val e))
    m))

(defn- as-row [row]
  (rename-key row :order :position))

(defn- as-todo [row]
  (rename-key row :position :order))

(def ^:private opts {:builder-fn rs/as-unqualified-lower-maps})

(defn create-store
  [ds]
  (reify p/Store
    (create-todos [this todos]
      (as-todo (sql/insert! ds :todos (as-row todos) opts)))
    (get-todo [this id]
      (as-todo (sql/get-by-id ds :todos id opts)))
    (update-todo [this id body]
      (sql/update! ds :todos (as-row body) {:id id})
      (p/get-todo this id))
    (delete-todo [this id]
      (sql/delete! ds :todos {:id id}))
    (get-all-todos [this]
      (jdbc/execute! ds ["SELECT * FROM todos;"] opts))
    (delete-all-todos [this]
      (sql/delete! ds :todos [true]))))

(defn jdbc-url
  []
  (System/getenv "JDBC_DATABASE_URL"))

(defn datasource
  [url]
  (jdbc/get-datasource url))
