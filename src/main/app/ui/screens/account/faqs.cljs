(ns app.ui.screens.account.faqs
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.svgs :refer [Svg]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["react-native-elements" :refer [SearchBar]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            [app.domain.faq :as faq]
            ["expo-linking" :as Linking]
            ["react-native-highlight-words" :default Highlighter]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(defn join-data [data]
  (reduce (fn [acc {:keys [answer question]}]
            (str acc (str question answer)))
          ""
          data))

(defnc PageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex :flex-1 :px-4 :pb-10 :mt-6)]} children))

(defnc Item [{:keys [data highlight]}]
  (let [[open? set-open] (hooks/use-state false)
        term (clojure.string/trim highlight)
        search-term (if (empty? term) #js[] #js[term])
        icon (if open? :arrow-up :arrow-down)]
    
    (hooks/use-memo [term]
                      (if (and (not-empty term) (boolean (re-find (re-pattern term) (:answer data))))
                        (set-open true)
                        (set-open false)))
    ($ View
       ($ TouchableOpacity
          {:onPress #(set-open (not open?))
           :style [(tw :w-full :flex-row :py-3 :flex :justify-between :items-center :border-b-2 :border-gray-lighter)]}
          ($ Text {:style [{:font-size 14}(tw :text-black)]}
             ($ Highlighter
                {:highlightStyle #js{:color "#000000"
                                     :backgroundColor "#F3BF4E"
                                     :fontWeight "700"}
                 :autoEscape true
                 :searchWords search-term
                 :textToHighlight (:question data)}))
          ($ View {:style [(tw :w-8 :h-8)]}
             ($ Svg {:type icon})))
       (when open?
         ($ View {:style [(tw :mt-2 :mb-5)]}
            ($ Text {:style [(tw :text-gray)
                             {:line-height 22
                              :font-size 17}]}
               ($ Highlighter
                  {:highlightStyle #js{:color "#000000"
                                       :backgroundColor "#F3BF4E"
                                       :fontWeight "700"}
                   :autoEscape true
                   :searchWords search-term
                   :textToHighlight (:answer data)})))))))

(defnc NoResults []
  ($ View {:style [(tw :mt-5)]}
     ($ Text {:style [(tw :text-gray :font-bold :text-lg :pb-5)]}
        "No results found.")
     ($ Text {:style [(tw :text-gray :text-lg)]}
        "Sorry, we weren't able to find any results for your search. Please check your spelling or try another term.")))

(defnc ScreenRenderer [props]
  (let [[value set-value] (hooks/use-state "")
        term (clojure.string/lower-case (clojure.string/trim value))
        all-data (clojure.string/lower-case (join-data faq/data))
        term-exists? (if (and (> (count term) 3) (not-empty term)) (boolean (re-find (re-pattern term) all-data)) true)]
    ($ KeyboardAwareScrollView
       ($ ScreenContainer
          ($ PageContainer
             ($ SearchBar
                {:value value
                 :onChangeText #(set-value %)
                 :placeholder "Search"
                 :containerStyle #js{:backgroundColor "transparent"
                                     :padding 0
                                     :margin 0
                                     :borderBottomColor "transparent"
                                     :borderTopColor "transparent"}
                 :inputContainerStyle #js{:backgroundColor "#F3F3F9"
                                          :borderRadius 18
                                          :padding 0
                                          :margin 0}
                 :inputStyle #js{:color "#5A5AF3"}
                 :searchIcon #js{:size 23}
                 :clearIcon nil})
             (if term-exists?
               ($ View
                  (when (and (not-empty term) (> (count term) 3))
                    ($ View {:style [(tw :mt-5)]}
                       ($ Text {:style [(tw :text-gray :text-lg)]}
                          (str "Searching FAQ for \"" term "\""))))
                  ($ View {:style [(tw :mt-10)]}
                     (map-indexed #($ Item {:key %1 :data %2 :highlight (if (> (count term) 3) value "")}) faq/data))
                  ($ View {:style [(tw :mt-5)]}
                     ($ Text {:style [(tw :text-gray)]}
                        "For further questions, please email "
                        ($ TouchableOpacity {:onPress #(ocall Linking :openURL "mailto:support@FlexCare.com")}($ Text {:style [(tw :text-purple :underline)]} "support@FlexCare.com.")))))
               ($ NoResults)))))))

(def Screen (with-keechma ScreenRenderer))
