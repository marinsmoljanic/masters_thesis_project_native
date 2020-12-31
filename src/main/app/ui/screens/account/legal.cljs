(ns app.ui.screens.account.legal
  (:require ["react-native" :refer [View Text TouchableOpacity Animated Linking]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            [app.rn.navigation :refer [navigate]]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            [app.util :refer [resolve-error]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(defnc LegalPageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex-1 :px-4 :pb-10 :pt-6)]} children))

(defnc Title [{:keys [title]}]
  ($ Text {:style [(tw :text-black)
                   {:font-size 20
                    :line-height 22
                    :font-weight "700"}]}
     title))

(defnc SmallGrayText [{:keys [title]}]
  ($ Text {:style [(tw :text-gray :pt-4)
                   :font-size 17
                   :line-height 22]}
     title))

(defnc BoldGrayTitle [{:keys [title]}]
  ($ Text {:style [(tw :text-gray :pt-4)
                   {:font-size 16
                    :line-height 22
                    :font-weight "600"}]}
     title))

(defnc ScreenRenderer [props]
  ($ ScreenContainer
     ($ KeyboardAwareScrollView
        ($ LegalPageContainer
           ($ Title {:title "Term & Conditions"})
           ($ SmallGrayText {:title
                             "This is NOT insurance nor is it intended to replace insurance. TeleHealth does not replace the primary care physician. There is no guarantee that a prescription will be written. Doctors operate subject to state regulation and may not be available in certain states. Doctors do not prescribe DEA controlled substances, non-therapeutic drugs and certain other drugs which may be harmful because of their potential for abuse. Doctors reserve the right to deny care for potential misuse of services. TeleHealth phone consultations are available 24 hours, 7 days a week, while video consultations are available during the hours of 7 am to 9 pm, 7 days a week in most states. TeleHealth is not insurance coverage and does not meet the minimum creditable coverage requirements under the Affordable Care Act or Massachusetts M.G.L. c. 111M and 956 CMR 5.00."})
           ($ View {:style [(tw :mt-6)]}
              ($ Title {:title "Privacy Policy"})
              ($ BoldGrayTitle {:title "NOTICE OF PRIVACY PRACTICES FOR FlexCare."})
              ($ SmallGrayText {:title "Effective: 9/28/2013"})
              ($ SmallGrayText {:title "Last Revised: 11/1/2015"}))
           ($ View {:style [(tw :mt-6)]}
              ($ BoldGrayTitle {:title "PLEASE REVIEW CAREFULLY."})
              ($ Text {:style [(tw :text-gray :pt-4)
                               :font-size 17
                               :line-height 22]}
                 "FlexCare, LLC (FlexCare) respects the privacy of each and every person and is committed to protecting all of your personal and PHI. This privacy policy applies to "
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} "https://www.FlexCare.com ")
                 ($ Text {:style [(tw :text-gray)
                                  :font-size 17
                                  :line-height 22]} "owned and operated by FlexCare, LLC."))
              ($ SmallGrayText {:title "This notice describes how personal information and health information (PHI) about you may be used, how it may be disclosed and how you can obtain access to this information. This page will serve as a summary of your privacy rights. The law (45 CFR Part 160 and Part 164, Subparts A and E) requires that your PHI be kept private. We must give you this Notice about our privacy practices and follow the terms of this Notice while it is in effect. Your use of FlexCare's Services indicates your acceptance of the terms of this Notice."})
              ($ Text {:style [(tw :text-gray)
                               :font-size 17
                               :line-height 22]}
                 "FlexCare has been awarded TRUSTe's Privacy Seal signifying that this privacy policy and practices have been reviewed by TRUSTe for compliance with "
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} "TRUSTe's program requirements ")
                 ($ Text {:style [(tw :text-gray :pt-4)
                                  :font-size 17
                                  :line-height 22]}
                    "including transparency, accountability and choice regarding the collection and use of your personal information. TRUSTe's mission, as an independent third party, is to accelerate online trust among consumers and organizations globally through its leading privacy trust mark and innovative trust solutions. If you have questions or complaints regarding our privacy policy or practices, please contact us at 1-800-317-0280. If you are not satisfied with our response you can contact ")
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} "TRUSTe here. ")
                 ($ Text {:style [(tw :text-gray :pt-4)
                                  :font-size 17
                                  :line-height 22]}
                    "The TRUSTe program covers only information that is collected through this Web site: http://www.mylifperx.com, and does not cover information that may be collected through software downloaded from the site. ")))
           ($ View
              ($ BoldGrayTitle {:title "I. INTRODUCTION "})
              ($ SmallGrayText {:title "FlexCare is engaged in the business of providing internet healthcare resources to connect individuals with physicians, licensed therapists and other licensed healthcare practitioners in real time, via live streaming video, telephone and/or secure e-mail for the diagnosis and treatment of patients over the Internet, as well as providing other types of administrative services (the “Services”)."}))
           ($ View
              ($ BoldGrayTitle {:title "II. INFORMATION THAT MAY BE COLLECTED "})
              ($ SmallGrayText {:title "In order to use the Services, you are asked to enter an e-mail address and password, which we refer to as your FlexCare ID or credentials. After you create your ID, you can use the same credentials to log in to FlexCare's website and utilize the Services. This log in process will allow you to manage your account, it will allow you to search physicians, make appointments, attend appointments, etc. "})
              ($ SmallGrayText {:title "The first time you log in to FlexCare's website to utilize the Services, you will be asked to create an account also known as your profile. To create an account, you must provide personal information such as name, address, telephone number, date of birth, e-mail address, gender, and other pertinent data that will be available for you to share with your Provider."})
              ($ SmallGrayText {:title "FlexCare will use the e-mail address you provide when you create your account to send you an e-mail requesting that you validate your account. Your e-mail address may also be used by FlexCare to provide appointment reminders, changes in appointments, messages from your physician, or Health related Programs as described in this Notice and the Terms of Use. FlexCare will use your e-mail address as the primary means to reset your username and password. Your e-mail address will not be shared with any other third parties and will not be used for advertising or sales purposes."})
              ($ SmallGrayText {:title "FlexCare creates a record of the care and services you receive. Some examples of the information collected or created through this process are video and/or audio files associated with all consultations, electronic medical records that may be uploaded or created as a result of treatment, and medical test results."})
              ($ SmallGrayText {:title "For certain services, such as for subscription, health sessions and monitoring services, we will collect credit card or billing/payment account information which we maintain in encrypted form on secure servers."})
              ($ SmallGrayText {:title "In order to participate in the Services, you will need to provide Personal Health Information about yourself to either FlexCare or the Providers. Here are examples of the types of Personal Health Information we gather:"})
              ($ SmallGrayText {:title "Information You Give Us - Examples of the types of information you may provide us include measurements, such as weight, blood pressure or glucose levels, lab results, medications, health history, and other health or PHI, such as prescription information."})
              ($ SmallGrayText {:title "Information Accessed through Third-Party Data Services - When you use our Services, we may access health-related information about you that is stored with third party-data services such as Microsoft HealthVault or Google Health, and such information will subsequently be available to us."})
              ($ SmallGrayText {:title "Third-Party Information - Health-related information about you received from third-parties (such as nurses, doctors or family members) as well as personally identifiable and other health-related information you provide specifically related to family members who may be utilizing the Services under your account."})
              ($ SmallGrayText {:title "Demographic Information, such as age, education, gender, Social Security Number and Zip Code."}))
           ($ View
              ($ BoldGrayTitle {:title "III. HOW INFORMATION ABOUT YOU MAY BE USED BY FlexCare "})
              ($ SmallGrayText {:title "FlexCare may gather PHI primarily to share with Providers for the purposes of diagnosis, treatment, and health care operations. However, in limited circumstances FlexCare may use de-identified, non-personal information for statistical analysis, improvement of the Services, and customization of web design and content layout. "})
              ($ BoldGrayTitle {:title "Uses and Disclosures of PHI "})
              ($ SmallGrayText {:title "FlexCare is permitted to use and disclose your PHI for purposes of (i) Treatment, (ii) Payment, and (iii) Health Care Operations as follows:"})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "For Treatment. ")
                 ($ SmallGrayText {:title "FlexCare may use or disclose your PHI to facilitate treatment or the provision of health care services to a Provider for purposes of a consultation or in connection with the provision of follow-up treatment. FlexCare may share your PHI with doctors, nurses, technicians, students or other FlexCare workers. For example, departments may share your PHI to plan your care. This may include prescriptions, lab work, and x-rays. FlexCare may share your PHI with people not at FlexCare including, but not limited to, referring physicians and home health care nurses who are treating you or providing follow-up care. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "For Payment. ")
                 ($ SmallGrayText {:title "FlexCare may use and disclose your PHI with others who help pay for your care such as health insurers or health plans in connection with the processing and payments of claims and other charges. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "For Health Care Operations. ")
                 ($ SmallGrayText {:title "FlexCare may use and disclose your PHI for its health care operations. These uses and disclosures help us run our programs and make sure FlexCare's patients receive quality care. For example, FlexCare may use PHI to review the treatment and provision services. FlexCare may use PHI to measure the performance of its staff and how they care for you. FlexCare may share PHI with third parties who FlexCare engages to provide various services for FlexCare and you such as doctors, nurses, technicians, students, and other health care workers for educational purposes. If any such third party requires access to your PHI in order to perform the agreed upon services, FlexCare will require that third party be bound to the terms outlined in this Privacy Notice. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Business Associates. ")
                 ($ SmallGrayText {:title "FlexCare may contract with outside businesses to provide some services. For example, FlexCare may use the services of transcription, laboratories or collection agencies. Each contracted party must enter into a Business Associate agreement with FlexCare, which requires said third party businesses to protect PHI that is shared with them in accordance with the restrictions outlined in this Privacy Notice. Furthermore, PHI will only be provided to third party businesses for the limited scope of performing required services to help facilitate treatment, payment, and health care operations to you. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "For Appointment Reminders. ")
                 ($ SmallGrayText {:title "FlexCare may contact you to remind you about your appointment for medical care. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Treatment Alternatives.  ")
                 ($ SmallGrayText {:title "FlexCare may use and disclose PHI to tell you about different types of treatment available to you. FlexCare may use and share PHI to tell you about other benefits and services related to your health. Authorization. FlexCare is permitted to use and disclose your PHI upon your written authorization, submitted on our form which will be provided to you upon request, to the extent that such use or disclosure is consistent with your authorization. Your written authorization is required for the release of any psychotherapy notes, marketing to you of any products or services not related to you care or treatment, or the sale of any information that is not de-identified. Please note that you may revoke or limit any such authorization at any time. FlexCare cannot take back any disclosures we have already made with your permission. FlexCare is required to keep records of the care that we provided to you. Be assured that any uses or disclosures not described in this notice will require your written authorization."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "People Involved In Your Care. ")
                 ($ SmallGrayText {:title "With your permission, FlexCare may share your PHI with a family member or friend who helps with your medical care. We may share your PHI with a group helping with disaster relief efforts. We do this so your family can be told about your location and condition. If you are not present or able to say no, we may use our judgment to decide if sharing your PHI is in your best interest. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Research. ")
                 ($ SmallGrayText {:title "In support of telemedicine and ehealth initiatives, FlexCare may use and disclose your PHI for research. FlexCare will only use and disclose information for research if FlexCare receives your written consent, or if a review committee that meets Federal standards says FlexCare does not need your consent. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Genetic Information. ")
                 ($ SmallGrayText {:title "FlexCare does not collect or use genetic information. FlexCare does not use genetic information for underwriting and related purposes."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Fundraising Activities. ")
                 ($ SmallGrayText {:title "FlexCare will not disclose your individual PHI for fundraising activities without your written authorization."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "As Required By Law.")
                 ($ SmallGrayText {:title "FlexCare may use and disclose your PHI when required to do so by federal, state or local law. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "To Prevent A Serious Threat To Health Or Safety. ")
                 ($ SmallGrayText {:title "FlexCare may use and disclose your PHI to prevent a serious threat to your health and safety and that of others. FlexCare will only disclose your PHI with persons who can help prevent the threat. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Testimonials. ")
                 ($ SmallGrayText {:title "We display personal testimonials of satisfied customers on our site in addition to other endorsements. With your consent we may post your testimonial along with your name."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "How FlexCare May Use and Disclose PHI - Special Situations  "))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Military. ")
                 ($ SmallGrayText {:title "If you are in the U.S. or foreign armed services, FlexCare may share your PHI as required by the proper military authorities. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Workers' Compensation. ")
                 ($ SmallGrayText {:title "FlexCare may share your PHI for workers' compensation or programs like it. FlexCare may do this to the extent required by law. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Public Health Risks. ")
                 ($ SmallGrayText {:title "FlexCare may share your PHI for public health activities, as required by federal, state or local law."}))
              ($ SmallGrayText {:title "For example, we may share your PHI: to prevent or control disease, injury or disability to report births and deaths to report child abuse or neglect to report reactions to medicines or problems with products to tell you about product recalls to tell you if you have been exposed to a disease or may be at risk for catching or spreading a disease or condition to tell the proper government department if FlexCare believes a patient has been the victim of abuse, neglect or domestic violence. FlexCare will only share this information when ordered or required by law."})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "De-identified Information. ")
                 ($ SmallGrayText {:title "FlexCare may use, disclose, and request PHI if the Health Information to be used or disclosed is de-identified pursuant to the procedures set forth in 45 CFR 145.514(a)-(c). Health Oversight Activities and Registries. FlexCare may share your PHI with government agencies that oversee health care. FlexCare may do so for activities approved by law. These activities include, but are not limited to, audits, investigations, inspections and licensure surveys. The government uses these activities to monitor the health care system. It also monitors the outbreak of disease, government programs, compliance with civil rights laws, and patient outcomes. FlexCare may share PHI with government registries, if required. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Lawsuits and Disputes. ")
                 ($ SmallGrayText {:title "If you are in a lawsuit or a dispute, FlexCare may share your PHI in response to a court order, legal demand or other lawful process. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Law Enforcement. ")
                 ($ SmallGrayText {:title "FlexCare may share PHI if asked to do so by a law enforcement official under limited circumstances as follows: to report certain types of wounds to respond to a court order, subpoena, warrant, summons or similar process to identify or locate a suspect, fugitive, material witness, or missing person about the victim of a crime, if under certain limited circumstances, FlexCare is unable to obtain the victim's agreement;"}))
              ($ SmallGrayText {:title "Decedents. FlexCare may, under limited circumstances, disclose your PHI to coroners, medical examiners, funeral directors for the purposes of identification, determining the cause of death and fulfilling duties relating to decedents. National Security. FlexCare may share, if required, your PHI with the proper federal officials for national security reasons. "})
              ($ BoldGrayTitle {:title "IV. HOW SECURITY IS HANDLED AT FlexCare "})
              ($ SmallGrayText {:title "The importance of security for all personal information including, but not limited to, PHI associated with you is of utmost concern to us. At FlexCare, we exercise state of the art care in providing secure transmission of your information from your PC or mobile device to our servers. PHI collected by our web site is stored in secure operation environments that are not available or accessible to the public. Only those employees who need access to your information in order to do their jobs are allowed access, each having signed confidentiality agreements. Any employee who violates our privacy or security policies is subject to disciplinary action, including possible termination and civil and/or criminal prosecution."})
              ($ SmallGrayText {:title "FlexCare is not only HIPPA compliant but additionally utilizes the latest technologies to ensure utmost security. FlexCare uses several layers of firewall security and different degrees of encryption for each customer's sensitive PHI to ensure the highest level of security which meets or exceeds the requirements promulgated under HIPAA (defined below) FlexCare Medical Group is the sole owner of the information collected on its site. FlexCare Medical Group will not sell, share or lease this information to others. FlexCare does not sell any customer lists, e-mail addresses, cookies or other data without your written authorization."})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Cookies and Tracking Technologies"))
              ($ SmallGrayText {:title "Cookies are text information files that your web browser places on your computer when you visit a website. Cookies assist in providing non-personal information from you as an online visitor. It can be used in the customization of your preferences when visiting our website. Most browsers accept cookies automatically, but can be configured not to accept them or to indicate when a cookie is being sent. FlexCare uses Google Analytics, a third-party tracking service, which uses cookies to track non-personal identifiable information about our visitors to our main site in the aggregate to capture usage and volume statistics. FlexCare has no access to or control over these cookies. This privacy policy covers the use of cookies by FlexCare only and does not cover the use of cookies by any third-party."})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Security on our Website"))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Secure interaction. ")
                 ($ SmallGrayText {:title "When you interact on our web site, all of your PHI including, but not limited to, your credit card number and delivery address, is transmitted through the Internet using Secure Socket Layers (SSL) technology. SSL technology causes your browser to encrypt your entered information before transmitting it to our secure server. SSL technology, an industry standard, is designed to prevent someone other than operators of our web site from capturing and viewing your personal information. FlexCare also takes the following measures to protect your PHI online: "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Passwords.  ")
                 ($ SmallGrayText {:title "To provide you with an increased level of security, online access to your PHI is protected with a password you select. We strongly recommend that you do not disclose your password to anyone. FlexCare will never ask you for your password in any unsolicited communication (including unsolicited correspondence such as letters, phone calls, or E-mail messages). "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Information.  ")
                 ($ SmallGrayText {:title "Since any entered information you provide to us on our website will be transmitted using a secure connection, if your web browser cannot support this level of security, you will not be able to order products through our website. The most recent versions of Safari, Netscape Navigator, Microsoft Internet Explorer and Firefox can support a secure connection and can be downloaded for free from their respective websites. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "No data transmission over the Internet can be guaranteed to be 100% secure.  ")
                 ($ SmallGrayText {:title "While we strive to protect your PHI from unauthorized access, use or disclosure, FlexCare cannot ensure or warrant the security of any information you transmit to us on our web site."}))
              ($ BoldGrayTitle {:title "V. Privacy Rights "})
              ($ SmallGrayText {:title "We are required by law to make sure that PHI that identifies you is kept private, give you this Notice of our legal duties and privacy practices concerning your PHI, and follow the terms of this Notice currently in effect. "})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Your Rights Regarding Your PHI"))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "You have the following rights regarding PHI FlexCare maintains about you: "))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To Inspect and To Receive Copies. ")
                 ($ SmallGrayText {:title "You have the right to view and receive copies of the PHI used to make decisions about your care, provided you submit your request in writing. Usually, this includes medical and billing records. It does not include some records such as psychotherapy notes. FlexCare may deny your request to view and/or copy your PHI in limited circumstances. If your request is denied, FlexCare will inform you of the reason of the denial and you have the right to request a review of the denial. FlexCare may charge a fee for the costs of processing your request. Contact Customer Service for more information at 1-800-317-0280."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To Amend. ")
                 ($ SmallGrayText {:title "If you think that personal information FlexCare has about you is wrong or incomplete, you have the right to ask for an amendment to your record. To request deletion of any personal information or ask for a change to your record, you must make your request in writing and submit it to Customer Service. If we are not able to comply with your request, we will respond with an explantation. FlexCare may, under the following limited circumstances:"}))
              ($ SmallGrayText {:title "FlexCare may deny your request for an amendment to your record. FlexCare may Deny your request if it is not submitted in writing or does not include a reason to support the request. FlexCare may also deny your request if you ask FlexCare to amend information that: FlexCare did not create, unless the person or entity that created the information is no longer available to make the amendment. Is not part of the records used to make decisions about you. Is not part of the information which you are permitted to inspect and to receive a copy; or is accurate and complete. Is not part of the information which you are permitted to inspect and to receive a copy; or is accurate and complete."})
              ($ SmallGrayText {:title "We will retain your information for as long as your account is active or as needed to provide you services. We will retain and use your information as necessary to comply with our legal obligations, resolve disputes, and enforce our agreements."})
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To an Accounting of Disclosures. ")
                 ($ SmallGrayText {:title "You have the right to get a list of the disclosures FlexCare has made of your PHI. This list will not include all disclosures that FlexCare made. For example, this list will not include disclosures that FlexCare made for treatment, payment or health care operations. It will not include disclosures made before June1, 2007, or disclosures you specifically approved. To ask for this list, you must submit your request in writing on the approved form. The form will be provided to you upon request. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To Request Restrictions. ")
                 ($ SmallGrayText {:title "You have the right to ask for a restriction or limitation on the PHI FlexCare uses or discloses for treatment, payment or health care operations. You also have the right to ask for a limit on the PHI FlexCare discloses with someone who is involved in your care or in the payment for your care. Such a person may be a family member or friend. FlexCare is not required to comply with your request. If FlexCare does agree, we will fulfill your request unless the information is needed to provide you with emergency treatment or if otherwise required by law. To ask for restrictions, you must make your request in writing on a form that we will give you upon request. You must tell us:"})
                 ($ SmallGrayText {:title "what information you want to limit, how you want us to limit the information, and to whom you want the limits to apply."}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To Request Confidential Communications. ")
                 ($ SmallGrayText {:title "You have the right to request confidential communications of your PHI or medical matters. You may request that FlexCare communicate with you through specific means or at a specific location. You must make your request in writing on a form that will be provided to you upon request. FlexCare will fulfill all reasonable requests. "}))
              ($ Text {:style [(tw :text-gray :pt-2)]}
                 ($ Text {:style [(tw :font-bold)]} "Right To a Paper Copy of This Notice. ")
                 ($ SmallGrayText {:title "You may ask FlexCare to give you a written copy of this Notice at any time. Even if you have agreed to get this Notice electronically, you still have a right to a paper copy of this Notice. If you click on a link to a third party site, you will leave the FlexCare site you are visiting and go to the site you selected. Because we cannot control the activities of third parties, we cannot accept responsibility for any use of your PHI by such third parties, and we cannot guarantee that they will adhere to the same privacy practices as FlexCare. We encourage you to review the privacy policies of any other service provider from whom you request services. If you visit a third party website that is linked to our site, you should read that site's privacy policy before providing any personal information."}))
              ($ BoldGrayTitle {:title "VI. Revisions To This Notice "})
              ($ SmallGrayText {:title "FlexCare is constantly innovating and implementing new features as part of its Services. As a result, our privacy practices may change. We may revise this Notice to reflect any changes in our privacy practices. We reserve the right to make the revised Notice effective for PHI we already have about you. It also will be effective for any information we receive in the future. We will post a current version of the Notice on this Site prior to the change becoming effective, as well as in the places where you receive the Services. The effective date of this Notice is on the first page, in the top, right-hand corner. If we make any material changes we will notify you by email (sent to the e-mail address specified in your account) or by means of a notice on this Site prior to the change becoming effective. "})
              ($ BoldGrayTitle {:title "VII. Single Sign-On"})
              ($ SmallGrayText {:title "You can choose to sign in to our site using sign-in services such as an Open ID provider. These services will authenticate your identity and provide you the option to share certain personal information with us such as your name and email address to pre-populate our sign up form. Services like an Open ID provider give you the option to post information about your activities on this Web site to your profile page to share with others within your network."})
              ($ BoldGrayTitle {:title "VIII. Social Media Widgets"})
              ($ SmallGrayText {:title "Our Web site includes Social Media Features, such as the Facebook button [and Widgets, such as the Share this button or interactive mini-programs that run on our site]. These Features may collect your IP address, which page you are visiting on our site, and may set a cookie to enable the Feature to function properly. Social Media Features and Widgets are either hosted by a third party or hosted directly on our Site. Your interactions with these Features are governed by the privacy policy of the company providing it."})
              ($ BoldGrayTitle {:title "IX. Complaints"})
              ($ Text {:style [(tw :text-gray :pt-4)
                               :font-size 17
                               :line-height 22]}
                 "If you think your privacy rights have been violated, you may file a complaint with "
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} "egal@FlexCare.com ")
                 ($ Text {:style [(tw :text-gray)
                                  :font-size 17
                                  :line-height 22]} " or in writing at the address listed below. You may also file a complaint with the Secretary of the Department of Health and Human Services. You will not be penalized for filing a complaint. If this does not satisfactorily resolve your inquiry then you may contact TRUSTe at ")
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} "http://www.truste.com/consumers/watchdog_complaint.php ")
                 ($ Text {:style [(tw :text-gray)
                                  :font-size 17
                                  :line-height 22]} "and TRUSTe will then serve as a liaison with this website to resolve your concerns. You may also contact us for further information about your privacy rights by emailing us at ")
                 ($ Text {:style [(tw :text-purple :underline)
                                  :font-size 17
                                  :line-height 22]} " support@FlexCare.com ")
                 ($ Text {:style [(tw :text-gray)
                                  :font-size 17
                                  :line-height 22]} "as well as by post mail:"))
              ($ SmallGrayText {:title "FlexCare"})
              ($ SmallGrayText {:title "3340 Peachtree Rd, NE Suite 1690"})
              ($ SmallGrayText {:title "Atlanta, GA 30326"})
              ($ SmallGrayText {:title "Attn: Privacy and Security Officer"})
              ($ BoldGrayTitle {:title "ACCEPTANCE"})
              ($ SmallGrayText {:title "By using this site and FlexCare's Services, you acknowledge your acceptance of FlexCare's privacy policy and agree to the terms described herein. If you do not agree with this policy, you should not use FlexCare's Services. It is recommended that you read this privacy policy before use of Services to ensure that you have not missed any changes to the privacy policy. Your continued use of the Services following any changes to the privacy policy signifies your acceptance of those changes."}))))))


(def Screen (with-keechma ScreenRenderer))
