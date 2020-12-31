(ns app.ui.components.modal
  (:require ["react-native" :refer [View TouchableOpacity Text]]
            ["react-native-modal" :default Modal]
            [app.lib :refer [$ defnc]]
            [keechma.next.helix.core :refer [use-sub dispatch with-keechma]]
            [app.ui.components.text :refer [FormTitle]]
            [app.tailwind :refer [tw]]
            [app.ui.svgs :refer [Svg]]))

(defnc ModalRenderer [props]
  (let [modal (use-sub props :modal)
        modal-visible? (get modal :open)
        modal-data (get modal :data)]
    ($ Modal {:isVisible modal-visible?
              :onSwipeComplete #(dispatch props :modal :close)
              :swipeDirection "up"}
       ($ View {:style [(tw :bg-white :rounded-18 :py-10 :px-4)]}
          ($ TouchableOpacity
             {:style [(tw :mr-4 :mt-4 :w-6 :h-6 :top-0 :right-0 :absolute)]
              :onPress #(dispatch props :modal :close)}
             ($ Svg {:type :close}))
          ($ View {:style [(tw :w-full :mb-5 :flex :justify-center)]}
             (:title modal-data))
          (:text modal-data)))))

(def DefaultModal (with-keechma ModalRenderer))
