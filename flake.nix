{
  description = "Skyla Advance POS Mobile - Android development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            android_sdk.accept_license = true;
          };
        };

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ "35.0.0" "34.0.0" ];
          platformVersions = [ "35" "34" ];
          abiVersions = [ "arm64-v8a" "x86_64" ];
          includeEmulator = false;
          includeSystemImages = false;
          includeSources = false;
          includeNDK = false;
          extraLicenses = [
            "android-googletv-license"
            "android-sdk-arm-dbt-license"
            "android-sdk-preview-license"
            "android-sdk-license"
            "google-gdk-license"
            "intel-android-extra-license"
            "intel-android-sysimage-license"
            "mips-android-sysimage-license"
          ];
        };

        androidSdk = androidComposition.androidsdk;
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk17
            androidSdk
            gradle
            kotlin
          ];

          JAVA_HOME = "${pkgs.jdk17}";
          ANDROID_HOME = "${androidSdk}/libexec/android-sdk";
          ANDROID_SDK_ROOT = "${androidSdk}/libexec/android-sdk";

          shellHook = ''
            export PATH="${androidSdk}/libexec/android-sdk/platform-tools:$PATH"
            echo "Skyla Advance POS - Android Dev Environment"
            echo "Java:    $(java -version 2>&1 | head -1)"
            echo "Gradle:  $(gradle --version 2>/dev/null | grep 'Gradle ' | head -1)"
            echo "Android: $ANDROID_HOME"
          '';
        };
      });
}
