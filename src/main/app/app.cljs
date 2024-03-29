(ns app.app
  (:require ["expo" :as ex]
            ["react" :as react]
            ["react-native" :refer [View Text]]
            [shadow.expo :as expo]
            ["react-native-safe-area-context" :refer [SafeAreaProvider]]
            [keechma.next.controllers.pipelines :refer [get-promise]]
            ["react-native-screens" :refer [enableScreens]]
            ["expo-splash-screen" :as SplashScreen]
            ["@use-expo/font" :refer [useFonts]]
            ["expo-av" :refer [Video]]
            [helix.hooks :as hooks]
            [oops.core :refer [oget ocall]]
            [app.main :refer [Root]]
            [app.lib :refer [defnc $]]
            [keechma.next.core :as keechma]
            [keechma.next.helix.core :refer [KeechmaRoot with-keechma use-sub use-meta-sub]]
            [app.keechma-app :refer [app]]
            [app.tailwind :refer [tw]]
            [promesa.core :as p]))

(enableScreens)

(defonce app-instance* (atom nil))

(defn ensure-fast-refresh-disabled! []
  (when debug?
    (ocall DevSettings :_nativeModule.setHotLoadingEnabled true)
    (ocall DevSettings :_nativeModule.setHotLoadingEnabled false)))

(defnc AppRenderer [props]
       ($ SafeAreaProvider
          ($ Root)))

(def App (with-keechma AppRenderer))

(defn reload
  {:dev/after-load true}
  []
  (ensure-fast-refresh-disabled!)
  (when-let [app-instance @app-instance*]
    (keechma/stop! app-instance))
  (let [app-instance (keechma/start! app)]
    (reset! app-instance* app-instance)
    (expo/render-root ($ KeechmaRoot {:keechma/app app-instance} ($ App)))))

(defn init []
  (reload))
