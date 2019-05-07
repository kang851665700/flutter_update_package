import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_update_plugin/flutter_update_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_update_plugin');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterUpdatePlugin.platformVersion, '42');
  });
}
