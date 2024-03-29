(ns app.ui.screens.project-edit
  (:require [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [oops.core :refer [oget ocall]]
            [app.util :refer [resolve-error]]

            [app.ui.svgs :refer [Svg]]
            [app.ui.components.buttons :as buttons]
            [app.ui.components.inputs :refer [wrapped-input]]
            [app.ui.components.text :refer [Subtitle FormTitle]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]

            [app.rn.animated :as a]
            [app.rn.animated :as animated]
            [keechma.next.toolbox.logging :as l]
            [app.rn.navigation :refer [navigate]]

            [keechma.next.controllers.form :as form]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]

            ["expo-av" :refer [Video]]
            ["expo-linking" :as Linking]
            ["react-native-modal" :default Modal]
            ["lottie-react-native" :as LottieView]
            ["@react-navigation/native" :refer [useNavigation]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["react-native" :refer [View Image Text TextInput ScrollView TouchableOpacity Animated]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc PageContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10 :mt-2)]} children))

(defnc TableHeader [_]
       ($ View {:style [(tw :flex :flex-row :bg-purple :mt-4)]}
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-r-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white :px-2 :text-center)
                              {:font-size 17
                               :line-height 22}]} "Osoba"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 ) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Uloga"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-l-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Datum zaduzenja"))))

(defnc TableItem [props]
       (let [_ (+ 1 1)]
            ($ View {:style [(tw :flex :flex-row)]}
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:person props)))
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:role props)))
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:assignmentDate props))))))

(defnc ClickableTableItem [{:keys [personName roleName assignmentDate project-data navigation]}]
       ($ TouchableOpacity
          {:onPress #(navigate navigation "person-role-by-project" #js{:firstName "Testno ime"
                                                                       :firstNamee "Tu idu ostale varijable"})
           :activeOpacity 0.9
           :style (tw :flex :flex-row :bg-white :w-full :border-b :border-l :border-r :border-solid :border-gray-light)}
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} personName))
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} roleName))
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} assignmentDate))))

(defnc EditProjectForm [props]
    ($ PageContainer
       (wrapped-input {:keechma.form/controller :project-edit-form
                       :input/type              :text
                       :input/attr              :name
                       :placeholder             "Naziv projekta"
                       :autoCapitalize          "none"})

       (wrapped-input {:keechma.form/controller :project-edit-form
                       :input/type              :text
                       :input/attr              :description
                       :placeholder             "Opis projekta"
                       :autoCapitalize          "none"})

       ($ View {:style [(tw "flex flex-row w-full items-center justify-center mt-8 border-solid pb-8 border-b border-gray-light")]}
          ($ buttons/Medium {:onPress #(dispatch props :project-edit-form :delete-project)
                             :title    "Obriši"
                             :style    [(tw :bg-purple)]
                             :text-style [(tw :text-white)]})
          ($ buttons/Medium {:onPress #(dispatch props :project-edit-form :keechma.form/submit)
                             :title    "Spremi"
                             :style    [(tw :bg-purple)]
                             :text-style [(tw :text-white)]}))))

(defnc ScreenRenderer [props]
       (let [navigation (useNavigation)
             [visible set-visible] (hooks/use-state false)
             image-uri (:uri (use-sub props :image-upload))
             image-upload-top-value (hooks/use-ref (animated/value 500))
             subtitle-top-value (hooks/use-ref (animated/value 500))
             animate-image-upload (hooks/use-ref (animated/timing @image-upload-top-value {:duration duration :to-value 0}))
             animate-subtitle (hooks/use-ref (animated/timing @subtitle-top-value {:duration duration :to-value 0}))
             person-roles (use-sub props :person-role-by-projectid)
             project-edit-data (use-sub props :project-edit-form)
             ]
            (hooks/use-effect :once
                              (-> (ocall Animated :stagger 200
                                         #js[@animate-image-upload
                                             @animate-subtitle])
                                  (animated/start)))

            ($ ScreenContainer
               ($ ScrollView {:scrollIndicatorInsets #js{:right 1}}
                  ($ KeyboardAwareScrollView
                     {:style [(tw :flex :flex-1)]
                      :enableOnAndroid true
                      :extraScrollHeight 75
                      :extraHeight 75
                      :keyboardShouldPersistTaps "always"}
                     ($ PageContainer
                        ($ AnimatedView {:style [(tw :relative)
                                                 {:top @subtitle-top-value
                                                  :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}
                           ($ EditProjectForm {& props})

                           ($ View {:style [(tw "flex flex-row justify-between w-full")]}
                              ($ Text {:style [(tw :text-gray-light :px-2 :text-center)
                                               {:font-size 14
                                                :line-height 22}]} "Zaduženja na projektu")

                              ($ buttons/Zaduzenje {:onPress #(navigate navigation "person-role")
                                                    :title    "+ Dodaj"
                                                    :style    [(tw :bg-purple)]
                                                    :text-style [(tw :text-white)]}))
                           ($ TableHeader)
                           (map (fn [person-role]
                                    ($ ClickableTableItem {:personId       (:PersonId person-role)
                                                           :roleId         (:RoleId person-role)
                                                           :personName     (:personName person-role)
                                                           :roleName       (:roleName person-role)
                                                           :assignmentDate (:AssignmentDate person-role)
                                                           :project-code   (:ProjectCode person-role)
                                                           :person-role-id (:id person-role)
                                                           :key            (:id person-role)
                                                           :project-data   project-edit-data

                                                           &               props}))
                                person-roles))))))))

(def Screen (with-keechma ScreenRenderer))
