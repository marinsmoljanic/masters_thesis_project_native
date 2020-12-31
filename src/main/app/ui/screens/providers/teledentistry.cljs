(ns app.ui.screens.providers.teledentistry
  (:require ["react-native" :refer [Text]]
            [app.ui.screens.providers.shared :refer [ProviderScreen]]
            [keechma.next.helix.core :refer [with-keechma]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.ui.components.text :refer [Subtitle]]))

(defnc ScreenRenderer [props]
  ($ ProviderScreen {:external-url-key :teledentistry
                     & props}))

(def Screen (with-keechma ScreenRenderer))
