#import "LeafFirstPlugin.h"
#import <leaf_first/leaf_first-Swift.h>

@implementation LeafFirstPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftLeafFirstPlugin registerWithRegistrar:registrar];
}
@end
