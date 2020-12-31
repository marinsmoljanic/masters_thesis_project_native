(ns app.ui.screens.providers.telespine
  (:require ["react-native" :refer [Text]]
            [app.ui.screens.providers.shared :refer [ProviderScreen]]
            [keechma.next.helix.core :refer [with-keechma]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.ui.components.text :refer [Subtitle]]))

(defnc ScreenRenderer [props]
  ($ ProviderScreen {:external-url-key :telespine
                     & props}))

(def Screen (with-keechma ScreenRenderer))
