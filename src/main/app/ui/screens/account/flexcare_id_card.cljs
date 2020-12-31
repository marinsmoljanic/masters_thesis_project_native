(ns app.ui.screens.account.flexcare-id-card
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [keechma.next.controllers.pipelines :refer [get-promise]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.hooks :refer [use-dimensions]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            ["expo-av" :refer [Video]]
            [app.ui.svgs :refer [Svg]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.ui.components.loader :refer [Loader]]
            [app.rn.animated :as animated]
            ["rn-pdf-reader-js" :default PDFReader]
            [oops.core :refer [oget]]
            ["@react-navigation/native" :refer [NavigationContainer useNavigation]]
            ["@react-navigation/bottom-tabs" :refer [createBottomTabNavigator]]
            [app.rn.navigation :refer [create-tab-navigator screen navigator]]
            ["expo-constants" :default Constants]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(def tab-navigator (createBottomTabNavigator))

(defnc PageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex :flex-1 :px-4 :items-center :justify-between :mt-10)]} children))

(defnc BackPdfRenderer [props]
  (let [device (first (keys (js->clj (oget Constants :platform))))
        [width set-width] (hooks/use-state "100%")
        member-id-card-meta (use-meta-sub props :member-id-card)
        pdf-url (use-sub props :member-id-card)
        pdf-back (second (clojure.string/split (:back pdf-url) #"\,"))
        base-64-back (str "data:application/pdf;base64," pdf-back)]
    (if (get-promise member-id-card-meta :load-pdf)
      ($ Loader)
      ($ View {:style [(tw :w-full :h-full)]}
         ($ View {:style [(tw :h-full :w-full)]}
            ($ PDFReader
               {:source #js{:base64 base-64-back}
                :webviewStyle (clj->js {:width width})
                :withPinchZoom true}))
         (when (= "ios" device)
           ($ View {:style [(tw :absolute :right-0 :top-0)]}
              ($ TouchableOpacity {:onPress #(set-width "150%")
                                   :style [(tw :absolute :right-0 :top-0 :w-8 :h-8 :mt-4 :mr-16)]}
                 ($ Svg {:type :search-plus}))
              ($ TouchableOpacity {:onPress #(set-width "100%")
                                   :style [(tw :absolute :right-0 :top-0 :w-8 :h-8 :mt-4 :mr-4)]}
                 ($ Svg {:type :search-minus}))))))))

(def BackPdf (with-keechma BackPdfRenderer))

(defnc FrontPdfRenderer [props]
  (let [device (first (keys (js->clj (oget Constants :platform))))
        [width set-width] (hooks/use-state "100%")
        member-id-card-meta (use-meta-sub props :member-id-card)
        pdf-url (use-sub props :member-id-card)
        pdf-front (second (clojure.string/split (:front pdf-url) #"\,"))
        base-64-front (str "data:application/pdf;base64," pdf-front)]
    (if (get-promise member-id-card-meta :load-pdf)
      ($ Loader)
      ($ View {:style [(tw :w-full :h-full)]}
         ($ View {:style [(tw :flex :flex-1)]}
            ($ PDFReader
               {:webviewStyle (clj->js {:width width
                                        :padding 0
                                        :margin 0})
                :source #js{:base64 base-64-front}
                :withPinchZoom true}))
         (when (= "ios" device)
           ($ View {:style [(tw :absolute :right-0 :top-0)]}
              ($ TouchableOpacity {:onPress #(set-width "150%")
                                   :style [(tw :absolute :right-0 :top-0 :w-8 :h-8 :mt-4 :mr-16)]}
                 ($ Svg {:type :search-plus}))
              ($ TouchableOpacity {:onPress #(set-width "100%")
                                   :style [(tw :absolute :right-0 :top-0 :w-8 :h-8 :mt-4 :mr-4)]}
                 ($ Svg {:type :search-minus}))))))))

(def FrontPdf (with-keechma FrontPdfRenderer))

(defnc TabBar [{:keys [index state-index route]}]
  (let [navigation (useNavigation)
        is-focused? (= state-index index)
        on-press #(navigate navigation (:name route))]
    ($ TouchableOpacity {:key (:key route)
                         :onPress on-press
                         :style [(tw :flex :flex-1 :h-full :justify-center :items-center)]}
       ($ Text {:style [(if is-focused? (tw :text-lg :text-purple) (tw :text-lg :text-gray-light))
                        {:font-weight "600"}]} (:name route)))))

(defnc TabBarWrapper [{:keys [state] :as props}]
  (let [routes (js->clj (oget state :routes) :keywordize-keys true)
        state-index (js->clj (oget state :index) :keywordize-keys true)]
    ($ View {:style [(tw :flex :flex-row :justify-center :items-center :h-20)]}
       (map-indexed #($ TabBar {:key (:key %2) :index %1 :state-index state-index :route %2}) routes))))


(defnc ScreenRenderer [props]
  ($ ScreenContainer
     ($ (oget tab-navigator :Navigator)
        {:tabBar #($ TabBarWrapper {& %})}
        ($ (oget tab-navigator :Screen)
           {:name "Front"
            :component FrontPdf})
        ($ (oget tab-navigator :Screen)
           {:name "Back"
            :component BackPdf}))))

(def Screen (with-keechma ScreenRenderer))
