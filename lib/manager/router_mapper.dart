class FlutterMapper {
  static const String _SUFFIX_NO_ANIM = "_NoAnim";

  static const String ROUTER_HOST = "/host";

  static const String ROUTER_HOME = "/home";

  static const String ROUTER_DETAIL = "/detail";

  static const String ROUTER_HOME_NO_ANIM = ROUTER_HOME + _SUFFIX_NO_ANIM;
  static const String ROUTER_DETAIL_NO_ANIM = ROUTER_DETAIL + _SUFFIX_NO_ANIM;

  static String noAnimRoute(String originalRoute) {
    return originalRoute + _SUFFIX_NO_ANIM;
  }
}

class NativeMapper {
  static const String NATIVE_VIEW = "/nativeView";
}
