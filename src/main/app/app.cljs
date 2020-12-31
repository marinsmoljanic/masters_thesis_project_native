(ns app.app
  (:require ["expo" :as ex]
            ["react" :as react]
            ["sentry-expo" :as sentry]
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

(ocall sentry :init #js{:dsn                     "https://830d7c7bdc4a4fe58f414f5dfeb10760@o243409.ingest.sentry.io/5542634"
                        :enableInExpoDevelopment true
                        :debug                   false})

(defonce app-instance* (atom nil))

(defonce splash-video (js/require "../assets/splash.mp4"))

(defn ensure-fast-refresh-disabled! []
  (when debug?
    (ocall DevSettings :_nativeModule.setHotLoadingEnabled true)
    (ocall DevSettings :_nativeModule.setHotLoadingEnabled false)))

(defn handle-video-ref [video-component set-video-ended]
  (let [status* (atom nil)]
    (when (and video-component (nil? @status*))
      (reset! status* :listener-set)
      (ocall video-component :setOnPlaybackStatusUpdate (fn [s]
                                                          (when (and (oget s "?isLoaded") (= @status* :listener-set))
                                                            (ocall SplashScreen :hideAsync)
                                                            (reset! status* :loaded))
                                                          (when (and (oget s "?didJustFinish") (= @status* :loaded))
                                                            (reset! status* :ended)
                                                            (set-video-ended true)))))))

(defn handle-jwt-promise [state value]
  (if (= value (:curr state))
    state
    {:prev (:curr state)
     :curr value}))

(defnc AppRenderer [props]
  (let [[fonts-loaded?]                (useFonts #js {"EBGaramond-ExtraBold" (js/require "../assets/fonts/EBGaramond-ExtraBold.ttf")})
        [video-ended? set-video-ended] (hooks/use-state false)
        jwt-meta                       (use-meta-sub props :jwt)
        jwt-promise-status             (p/promise? (get-promise jwt-meta :load-jwt))
        [jwt-state set-jwt-state]      (hooks/use-state {:prev nil
                                                         :curr jwt-promise-status})
        jwt                            (use-sub props :jwt)
        jwt-check-finish?              (or (and (seq jwt) (:prev jwt-state) (false? (:curr jwt-state))) ;; if has jwt and verify-jwt has finished
                                           (nil? jwt)) ;; or if jwt hasn't been set yet
        ]
    (hooks/use-effect :once (ocall SplashScreen :preventAutoHideAsync))
    (hooks/use-effect :always (set-jwt-state (handle-jwt-promise jwt-state jwt-promise-status)))    
    (if (and fonts-loaded? video-ended? jwt-check-finish?)
      ($ SafeAreaProvider
         ($ Root))
      ($ View {:style [(tw :items-center :justify-between :w-full :h-full :bg-purple)]}
         ($ Video {:isMuted    true
                   :shouldPlay true
                   :style      [(tw :w-full :h-full)]
                   :resizeMode "cover"
                   :source     splash-video
                   :ref        #(handle-video-ref % set-video-ended)})))))

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
