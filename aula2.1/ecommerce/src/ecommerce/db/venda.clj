(ns ecommerce.db.venda
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]))

(defn adiciona!
  [conn produto-id quantidade]
  (let [id (model/uuid)]
    (d/transact conn [{:db/id            "venda"
                       :venda/produto    [:produto/id produto-id]
                       :venda/quantidade quantidade
                       :venda/id         id}])
    id))