(defproject interprocessing "1.0"
  :url "https://github.com/andereld/interprocessing"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [net.mikera/core.matrix "0.49.0"]]
  :main ^:skip-aot interprocessing.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
