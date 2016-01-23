(ns pandeiro.boot-http-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pandeiro.boot-http.impl :refer [dir-handler index-file-exists?]]
            [pandeiro.boot-http.util :as u]))

(deftest detect-index-files
  (let [index-html  (io/file "index.html")
        index-htm   (io/file "index.htm")
        INDEX-HTML  (io/file "INDEX.HTML")
        index-shtml (io/file "index.shtml")]
    (testing "recognize index files"
      (is (= index-html (index-file-exists? [index-html])))
      (is (= index-htm  (index-file-exists? [index-htm])))
      (is (= INDEX-HTML (index-file-exists? [INDEX-HTML]))))
    (testing "ignore non-index files"
      (is (nil? (index-file-exists? [index-shtml]))))))

(deftest resolve-symbols
  (testing "can resolve ns-qualified symbols"
    (is (= "sample"
           @(u/resolve-sym 'pandeiro.boot-http-tests-sample/sample-var))))
  (testing "can invoke ns-qualified functions"
    (is (= :sample
           (u/resolve-and-invoke 'pandeiro.boot-http-tests-sample/sample-fn)))))

(defn teapot-app [request]
  {:status 418})

(deftest not-found-handler
  (let [req {:uri "/missing"}]
    (testing "is called when serving a directory"
      (is (= 404
             (:status ((dir-handler {:dir "public"}) req))))
      (is (= 418
             (:status ((dir-handler {:dir "public" :not-found `teapot-app}) req)))))))
