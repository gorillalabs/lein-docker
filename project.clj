(defproject gorillalabs/lein-docker "0.0.0"
  :description "A leiningen plugin to build docker images and deploy them."
  :url "https://github.com/gorillalabs/lein-docker"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :middleware [leiningen.v/dependency-version-from-scm
               leiningen.v/version-from-scm
               leiningen.v/add-workspace-data]
  :plugins [[com.roomkey/lein-v "6.2.0"]]

  :min-lein-version "2.5.0"
  :eval-in-leiningen true

  :dependencies []

  :vcs :git
  :scm {:name "git"
        :url  "https://github.com/gorillalabs/lein-docker.git"}

  :deploy-repositories [["releases" :clojars]]
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"]
                  ["deploy"]
                  ["vcs" "push"]])
