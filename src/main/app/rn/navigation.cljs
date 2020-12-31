(ns app.rn.navigation
  (:require ["@react-navigation/stack" :refer [createStackNavigator]]
            ["@react-navigation/bottom-tabs" :refer [createBottomTabNavigator]]
            [oops.core :refer [oget ocall]]))

(def create-stack-navigator createStackNavigator)

(def create-tab-navigator createBottomTabNavigator)

(defn screen [stack]
  (oget stack :Screen))

(defn navigator [stack]
  (oget stack :Navigator))

(defn navigate
  ([navigation target]
   (ocall navigation :navigate target))
  ([navigation target params]
   (ocall navigation :navigate target params)))