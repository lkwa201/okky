package net.okjsp

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.mobile.device.Device

class BannerTagLib {

    def randomService
    def deviceResolver

    /**
     * @attr type REQUIRED
     */
    def banner = { attrs, body ->
        
        def bannerType = attrs.type
        
        if(!(bannerType instanceof BannerType)) bannerType = BannerType.valueOf(bannerType)

        def banners = Banner.where {
            type == bannerType && visible == true
        }.list()

        def banner = banners ? randomService.draw(banners) : null
        def bannerHTML = ""


        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        def request = webUtils.getCurrentRequest()

        Device device =  deviceResolver.resolveDevice(request)


        if(banner) {

            def target = """target=\"${banner.target}\""""
            switch (bannerType) {
                
                case BannerType.MAIN_RIGHT :
                case BannerType.SUB_RIGHT :
                    bannerHTML = """<div class="right-banner">
                                    <a href="${request.contextPath}/banner/stats/${banner.id}" ${banner.target ? target : ''}><img src="${banner.image}" style="width:160px;"/></a>
                                </div>"""

                    break
                case BannerType.MAIN : 
                    bannerHTML = """<div class="main-banner-wrapper">
                                        <div class="main-banner"><a href="${request.contextPath}/banner/stats/${banner.id}" ${banner.target ? target : ''}><img src="${banner.image}" /></a></div>
                                    </div>"""
                    break
                case BannerType.CONTENT :
                    bannerHTML = """<div class="sub-banner-wrapper">
                                        <div class="sub-banner"><a href="${request.contextPath}/banner/stats/${banner.id}" ${banner.target ? target : ''}><img src="${banner.image}" /></a></div>
                                    </div>"""
                    break
            }
        }

        if(bannerType == BannerType.MAIN_RIGHT || bannerType == BannerType.SUB_RIGHT) {

           /* if(device.isMobile()) {
                bannerHTML = """
                            <script type="text/javascript" src="http://ad.appsary.com/ad/22019/tag.js"></script>
                        """
            } else {*/

                bannerHTML += """<div class="google-ad">
                        <script type="text/javascript"><!--
                        google_ad_client = "ca-pub-1191230850516122";
                        /* adxguru\\okky.kr\\160x600\\0 */
                        google_ad_slot = "3902767047";
                        google_ad_width = 160;
                        google_ad_height = 600;
                        //-->
                        </script>
                        <script type="text/javascript"
                        src="//pagead2.googlesyndication.com/pagead/show_ads.js">
                        </script>
                     </div>"""
//            }
        }

        out << bannerHTML
    }
}
