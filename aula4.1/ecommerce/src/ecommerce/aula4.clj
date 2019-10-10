(ns ecommerce.aula4
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.config :as db.config]
            [ecommerce.db.produto :as db.produto]
            [schema.core :as s]
            [ecommerce.db.venda :as db.venda]))

(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(def produtos (db.produto/todos (d/db conn)))
(def primeiro (first produtos))
(pprint primeiro)

(def venda1 (db.venda/adiciona! conn (:produto/id primeiro) 3))
(def venda2 (db.venda/adiciona! conn (:produto/id primeiro) 4))
(def venda3 (db.venda/adiciona! conn (:produto/id primeiro) 8))
(pprint venda1)

(pprint @(db.venda/cancela! conn venda1))

(pprint (count (db.venda/todas-nao-canceladas (d/db conn))))

(pprint (count (db.venda/todas-inclusive-canceladas (d/db conn))))
(pprint (count (db.venda/canceladas (d/db conn))))

















