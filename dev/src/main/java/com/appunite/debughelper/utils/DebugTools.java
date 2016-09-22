package com.appunite.debughelper.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;

public class DebugTools {

    public static String checkSDKName(final int sdkInt) {
        switch (sdkInt) {
            case Build.VERSION_CODES.BASE: // API level 1
                return "Base";
            case Build.VERSION_CODES.BASE_1_1: // API level 2
                return "Base update";
            case Build.VERSION_CODES.CUPCAKE: // API level 3
                return "Cupcake";
            case Build.VERSION_CODES.CUR_DEVELOPMENT: // API level 4
                return "Cur development";
            case Build.VERSION_CODES.DONUT: // API level 5
                return "Donut";
            case Build.VERSION_CODES.ECLAIR: // API level 6
                return "Eclair";
            case Build.VERSION_CODES.ECLAIR_0_1: // API level 7
                return "Eclair 0 1";
            case Build.VERSION_CODES.ECLAIR_MR1: // API level 8
                return "Eclair MR1";
            case Build.VERSION_CODES.FROYO: // API level 9
                return "Froyo";
            case Build.VERSION_CODES.GINGERBREAD: // API level 10
                return "Gingerbread";
            case Build.VERSION_CODES.GINGERBREAD_MR1: // API level 11
                return "Gingerbread MR1";
            case Build.VERSION_CODES.HONEYCOMB: // API level 12
                return "Honeycomb";
            case Build.VERSION_CODES.HONEYCOMB_MR1: // API level 13
                return "Honeycomb MR1";
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                return "Honeycomb MR2";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH: // API level 14
                return "Ice Cream Sandwich";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1: // API level 15
                return "Ice Cream Sandwich MR1";
            case Build.VERSION_CODES.JELLY_BEAN: // API level 16
                return "Jelly Bean";
            case Build.VERSION_CODES.JELLY_BEAN_MR1: // API level 17
                return "Jelly Bean MR1";
            case Build.VERSION_CODES.JELLY_BEAN_MR2: // API level 18
                return "Jelly Bean MR2";
            case Build.VERSION_CODES.KITKAT: // API level 19
                return "Kitkat";
            case Build.VERSION_CODES.KITKAT_WATCH: //API 20
                return "Kitkat Watch";
            case Build.VERSION_CODES.LOLLIPOP: //API 21
                return "Lollipop";
            case Build.VERSION_CODES.LOLLIPOP_MR1: //API 22
                return "Lollipop MR1";
            case 23:
                return "Marshmallow";
            case 24 :
                return "Nougat";
            default:
                return "Unknown";
        }
    }

    public static int selectHttpCodePosition(final int code) {
        switch (code) {
            /**
             * Numeric status code, 200: OK
             */
            case HttpsURLConnection.HTTP_OK:
                return 0;
            /**
             * Numeric status code, 201: Created
             */
            case HttpsURLConnection.HTTP_CREATED:
                return 1;
            /**
             * Numeric status code, 202: Accepted
             */
            case HttpsURLConnection.HTTP_ACCEPTED:
                return 2;
            /**
             * Numeric status code, 203: Not authoritative
             */
            case HttpsURLConnection.HTTP_NOT_AUTHORITATIVE:
                return 3;
            /**
             * Numeric status code, 204: No content
             */
            case HttpsURLConnection.HTTP_NO_CONTENT:
                return 4;
            /**
             * Numeric status code, 205: Reset
             */
            case HttpsURLConnection.HTTP_RESET:
                return 5;
            /**
             * Numeric status code, 206: Partial
             */
            case HttpsURLConnection.HTTP_PARTIAL:
                return 6;
            /**
             * Numeric status code, 300: Multiple choices
             */
            case HttpsURLConnection.HTTP_MULT_CHOICE:
                return 7;
            /**
             * Numeric status code, 301 Moved permanently
             */
            case HttpsURLConnection.HTTP_MOVED_PERM:
                return 8;
            /**
             * Numeric status code, 302: Moved temporarily
             */
            case HttpsURLConnection.HTTP_MOVED_TEMP:
                return 9;
            /**
             * Numeric status code, 303: See other
             */
            case HttpsURLConnection.HTTP_SEE_OTHER:
                return 10;
            /**
             * Numeric status code, 304: Not modified
             */
            case HttpsURLConnection.HTTP_NOT_MODIFIED:
                return 11;
            /**
             * Numeric status code, 305: Use proxy.
             */
            case HttpsURLConnection.HTTP_USE_PROXY:
                return 12;
            /**
             * Numeric status code, 400: Bad Request
             */
            case HttpsURLConnection.HTTP_BAD_REQUEST:
                return 13;
            /**
             * Numeric status code, 401: Unauthorized
             */
            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                return 14;
            /**
             * Numeric status code, 402: Payment required
             */
            case HttpsURLConnection.HTTP_PAYMENT_REQUIRED:
                return 15;
            /**
             * Numeric status code, 403: Forbidden
             */
            case HttpsURLConnection.HTTP_FORBIDDEN:
                return 16;
            /**
             * Numeric status code, 404: Not found
             */
            case HttpsURLConnection.HTTP_NOT_FOUND:
                return 17;
            /**
             * Numeric status code, 405: Bad Method
             */
            case HttpsURLConnection.HTTP_BAD_METHOD:
                return 18;
            /**
             * Numeric status code, 406: Not acceptable
             */
            case HttpsURLConnection.HTTP_NOT_ACCEPTABLE:
                return 19;
            /**
             * Numeric status code, 407: Proxy authentication required
             */
            case HttpsURLConnection.HTTP_PROXY_AUTH:
                return 20;
            /**
             * Numeric status code, 408: Client Timeout
             */
            case HttpsURLConnection.HTTP_CLIENT_TIMEOUT:
                return 21;
            /**
             * Numeric status code, 409: Conflict
             */
            case HttpsURLConnection.HTTP_CONFLICT:
                return 22;
            /**
             * Numeric status code, 410: Gone
             */
            case HttpsURLConnection.HTTP_GONE:
                return 23;
            /**
             * Numeric status code, 411: Length required
             */
            case HttpsURLConnection.HTTP_LENGTH_REQUIRED:
                return 24;
            /**
             * Numeric status code, 412: Precondition failed
             */
            case HttpsURLConnection.HTTP_PRECON_FAILED:
                return 25;
            /**
             * Numeric status code, 413: Entity too large
             */
            case HttpsURLConnection.HTTP_ENTITY_TOO_LARGE:
                return 26;
            /**
             * Numeric status code, 414: Request too long
             */
            case HttpsURLConnection.HTTP_REQ_TOO_LONG:
                return 27;
            /**
             * Numeric status code, 415: Unsupported type
             */
            case HttpsURLConnection.HTTP_UNSUPPORTED_TYPE:
                return 28;
            /**
             * Numeric status code, 500: Internal error
             */
            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                return 29;
            /**
             * Numeric status code, 501: Not implemented
             */
            case HttpsURLConnection.HTTP_NOT_IMPLEMENTED:
                return 30;
            /**
             * Numeric status code, 502: Bad Gateway
             */
            case HttpsURLConnection.HTTP_BAD_GATEWAY:
                return 31;
            /**
             * Numeric status code, 503: Unavailable
             */
            case HttpsURLConnection.HTTP_UNAVAILABLE:
                return 32;
            /**
             * Numeric status code, 504: Gateway timeout
             */
            case HttpsURLConnection.HTTP_GATEWAY_TIMEOUT:
                return 33;
            /**
             * Numeric status code, 505: Version not supported
             */
            case HttpsURLConnection.HTTP_VERSION:
                return 34;
            default:
                return 0;
        }
    }

    public static String getBuildVersion(@Nonnull final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public static String getBuildType(@Nonnull final Context context) {
        final boolean isDebuggable = (0 != (context.getApplicationInfo().flags & context.getApplicationInfo().FLAG_DEBUGGABLE));
        if (isDebuggable) {
            return "debug";
        } else {
            return "Release";

        }
    }

    public static Boolean isDebuggable(@Nonnull final Context context) {
        return (0 != (context.getApplicationInfo().flags & context.getApplicationInfo().FLAG_DEBUGGABLE));
    }

    public static String getApplicationName(@Nonnull final Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }
}
