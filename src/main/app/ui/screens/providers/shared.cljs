(ns app.ui.screens.providers.shared
  (:require ["react-native-webview" :refer [WebView]]
            [keechma.next.helix.core :refer [use-sub with-keechma]]
            [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.domain.providers :refer [get-external-url]]
            [app.ui.components.loader :refer [Loader]]))

(defnc ProviderScreenRenderer [{:keys [external-url-key] :as props}]
  (let [jwt (use-sub props :jwt)
        [webview-loaded? set-webview-loaded] (hooks/use-state false)]
    ($ ScreenContainer
       (when (not webview-loaded?)
         ($ Loader {:style [{:zIndex 3 :elevation 3} (tw :w-full :h-full :absolute :top-0 :bottom-0 :left-0 :right-0)]}))
       ($ WebView {:style [(tw :w-full :h-full)]
                   :onLoadEnd #(set-webview-loaded true)
                   :source #js {:uri (get-external-url external-url-key jwt)}}))))

(def ProviderScreen (with-keechma ProviderScreenRenderer))