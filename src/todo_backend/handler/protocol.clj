(ns todo-backend.handler.protocol)

(defprotocol Handler
  (get-all-todos [this request])
  (create-todos [this request])
  (delete-all-todos [this])
  (get-todo [this request])
  (update-todo [this request])
  (delete-todo [this request]))
