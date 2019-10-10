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

(defn instante-da-venda [db venda-id]
  (d/q '[:find ?instante .
         :in $ ?id
         :where [_ :venda/id ?id ?tx true]
         [?tx :db/txInstant ?instante]]
       db venda-id))

; executamos em duas queries, poderíamos executar em uma só
; fazendo junções com nested queries ou novas condições
(defn custo [db venda-id]
  (let [instante (instante-da-venda db venda-id)]
    (d/q '[:find (sum ?preco-por-produto) .
           :in $ ?id
           :where [?venda :venda/id ?id]
           [?venda :venda/quantidade ?quantidade]
           [?venda :venda/produto ?produto]
           [?produto :produto/preco ?preco]
           [(* ?preco ?quantidade) ?preco-por-produto]]
         (d/as-of db instante) venda-id)))
