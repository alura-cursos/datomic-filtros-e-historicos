(ns ecommerce.db.venda
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]
            [ecommerce.db.entidade :as db.entidade]))

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

(defn cancela! [conn venda-id]
  (d/transact conn [[:db/retractEntity [:venda/id venda-id]]]))

(defn todas-nao-canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]]
       db))



(defn todas-inclusive-canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id _ true]]
       (d/history db)))


(defn canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id _ false]]
       (d/history db)))











