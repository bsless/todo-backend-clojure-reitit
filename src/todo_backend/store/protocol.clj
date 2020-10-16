(ns todo-backend.store.protocol)

(defprotocol Store
  (create-todos [this todos])
  (get-todo [this id])
  (update-todo [this id body])
  (delete-todo [this id])
  (get-all-todos [this])
  (delete-all-todos [this]))
