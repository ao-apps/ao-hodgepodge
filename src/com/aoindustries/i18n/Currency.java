/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.i18n;

import java.util.Locale;

/**
 * ISO 4217 currency codes.
 *
 * Source: http://en.wikipedia.org/wiki/ISO_4217#Active_codes on 2009-12-14
 *
 * @author  AO Industries, Inc.
 */
public enum Currency implements LocalizedToString {

    AED(Country.AE, "784", 2),
    AFN(Country.AF, "971", 2),
    ALL(Country.AL, "008", 2),
    AMD(Country.AM, "051", 0),
    ANG(Country.AN, "532", 2),
    AOA(Country.AO, "973", 1),
    ARS(Country.AR, "032", 2),
    AUD(Country.AU, "036", 2),
    AWG(Country.AW, "533", 2),
    AZN(Country.AZ, "944", 2),
    BAM(Country.BA, "977", 2),
    BBD(Country.BB, "052", 2),
    BDT(Country.BD, "050", 2),
    BGN(Country.BG, "975", 2),
    BHD(Country.BH, "048", 3),
    BIF(Country.BI, "108", 0),
    BMD(Country.BM, "060", 2),
    BND(Country.BN, "096", 2),
    BOB(Country.BO, "068", 2),
    BOV(Country.BO, "984", 2),
    BRL(Country.BR, "986", 2),
    BSD(Country.BS, "044", 2),
    BTN(Country.BT, "064", 2),
    BWP(Country.BW, "072", 2),
    BYR(Country.BY, "974", 0),
    BZD(Country.BZ, "084", 2),
    CAD(Country.CA, "124", 2),
    CDF(Country.CD, "976", 2),
    CHE(Country.CH, "947", 2),
    CHF(Country.CH, "756", 2),
    CHW(Country.CH, "948", 2),
    CLF(Country.CL, "990", 0),
    CLP(Country.CL, "152", 0),
    CNY(Country.CN, "156", 1),
    COP(Country.CO, "170", 0),
    COU(Country.CO, "970", 2),
    CRC(Country.CR, "188", 2),
    CUC(Country.CU, "931", 2),
    CUP(Country.CU, "192", 2),
    CVE(Country.CV, "132", 2),
    CZK(Country.CZ, "203", 2),
    DJF(Country.DJ, "262", 0),
    DKK(Country.DK, "208", 2),
    DOP(Country.DO, "214", 2),
    DZD(Country.DZ, "012", 2),
    EEK(Country.EE, "233", 2),
    EGP(Country.EG, "818", 2),
    ERN(Country.ER, "232", 2),
    ETB(Country.ET, "230", 2),
    EUR(null,       "978", 2),
    FJD(Country.FJ, "242", 2),
    FKP(Country.FK, "238", 2),
    GBP(Country.GB, "826", 2),
    GEL(Country.GE, "981", 2),
    GHS(Country.GH, "936", 2),
    GIP(Country.GI, "292", 2),
    GMD(Country.GM, "270", 2),
    GNF(Country.GN, "324", 0),
    GTQ(Country.GT, "320", 2),
    GYD(Country.GY, "328", 2),
    HKD(Country.HK, "344", 2),
    HNL(Country.HN, "340", 2),
    HRK(Country.HR, "191", 2),
    HTG(Country.HT, "332", 2),
    HUF(Country.HU, "348", 2),
    IDR(Country.ID, "360", 0),
    ILS(Country.IL, "376", 2),
    INR(Country.IN, "356", 2),
    IQD(Country.IQ, "368", 0),
    IRR(Country.IR, "364", 0),
    ISK(Country.IS, "352", 0),
    JMD(Country.JM, "388", 2),
    JOD(Country.JO, "400", 3),
    JPY(Country.JP, "392", 0),
    KES(Country.KE, "404", 2),
    KGS(Country.KG, "417", 2),
    KHR(Country.KH, "116", 0),
    KMF(Country.KM, "174", 0),
    KPW(Country.KP, "408", 0),
    KRW(Country.KR, "410", 0),
    KWD(Country.KW, "414", 3),
    KYD(Country.KY, "136", 2),
    KZT(Country.KZ, "398", 2),
    LAK(Country.LA, "418", 0),
    LBP(Country.LB, "422", 0),
    LKR(Country.LK, "144", 2),
    LRD(Country.LR, "430", 2),
    LSL(Country.LS, "426", 2),
    LTL(Country.LT, "440", 2),
    LVL(Country.LV, "428", 2),
    LYD(Country.LY, "434", 3),
    MAD(Country.MA, "504", 2),
    MDL(Country.MD, "498", 2),
    MGA(Country.MG, "969", 0),
    MKD(Country.MK, "807", 2),
    MMK(Country.MM, "104", 0),
    MNT(Country.MN, "496", 2),
    MOP(Country.MO, "446", 1),
    MRO(Country.MR, "478", 0),
    MUR(Country.MU, "480", 2),
    MVR(Country.MV, "462", 2),
    MWK(Country.MW, "454", 2),
    MXN(Country.MX, "484", 2),
    MXV(Country.MX, "979", 2),
    MYR(Country.MY, "458", 2),
    MZN(Country.MZ, "943", 2),
    NAD(Country.NA, "516", 2),
    NGN(Country.NG, "566", 2),
    NIO(Country.NI, "558", 2),
    NOK(Country.NO, "578", 2),
    NPR(Country.NP, "524", 2),
    NZD(Country.NZ, "554", 2),
    OMR(Country.OM, "512", 3),
    PAB(Country.PA, "590", 2),
    PEN(Country.PE, "604", 2),
    PGK(Country.PG, "598", 2),
    PHP(Country.PH, "608", 2),
    PKR(Country.PK, "586", 2),
    PLN(Country.PL, "985", 2),
    PYG(Country.PY, "600", 0),
    QAR(Country.QA, "634", 2),
    RON(Country.RO, "946", 2),
    RSD(Country.RS, "941", 2),
    RUB(Country.RU, "643", 2),
    RWF(Country.RW, "646", 0),
    SAR(Country.SA, "682", 2),
    SBD(Country.SB, "090", 2),
    SCR(Country.SC, "690", 2),
    SDG(Country.SD, "938", 2),
    SEK(Country.SE, "752", 2),
    SGD(Country.SG, "702", 2),
    SHP(Country.SH, "654", 2),
    SLL(Country.SL, "694", 0),
    SOS(Country.SO, "706", 2),
    SRD(Country.SR, "968", 2),
    STD(Country.ST, "678", 0),
    SYP(Country.SY, "760", 2),
    SZL(Country.SZ, "748", 2),
    THB(Country.TH, "764", 2),
    TJS(Country.TJ, "972", 2),
    TMT(Country.TM, "934", 2),
    TND(Country.TN, "788", 3),
    TOP(Country.TO, "776", 2),
    TRY(Country.TR, "949", 2),
    TTD(Country.TT, "780", 2),
    TWD(Country.TW, "901", 1),
    TZS(Country.TZ, "834", 2),
    UAH(Country.UA, "980", 2),
    UGX(Country.UG, "800", 0),
    USD(Country.US, "840", 2),
    USN(Country.US, "997", 2),
    USS(Country.US, "998", 2),
    UYU(Country.UY, "858", 2),
    UZS(Country.UZ, "860", 2),
    VEF(Country.VE, "937", 2),
    VND(Country.VN, "704", 0),
    VUV(Country.VU, "548", 0),
    WST(Country.WS, "882", 2),
    XAF(null,       "950", 0),
    XAG(null,       "961", 4),
    XAU(null,       "959", 4),
    XBA(null,       "955", 4),
    XBB(null,       "956", 4),
    XBC(null,       "957", 4),
    XBD(null,       "958", 4),
    XCD(null,       "951", 2),
    XDR(null,       "960", 4),
    XFU(null,       "Nil", 4),
    XOF(null,       "952", 0),
    XPD(null,       "964", 4),
    XPF(null,       "953", 0),
    XPT(null,       "962", 4),
    XTS(null,       "963", 4),
    XXX(null,       "999", 4),
    YER(Country.YE, "886", 0),
    ZAR(Country.ZA, "710", 2),
    ZMK(Country.ZM, "894", 0),
    ZWD(Country.ZW, "932", 2);

    private final Country country;
    private final String num;
    private final int scale;

    private Currency(Country country, String num, int scale) {
        this.country = country;
        this.num = num;
        this.scale = scale;
    }

    /**
     * Gets the country for the currency or <code>null</code> if the currency
     * is not part of a specific country, such as gold.
     */
    public Country getCountry() {
        return country;
    }

    public String getNum() {
        return num;
    }

    /**
     * Gets the number of decimal places to the right.
     */
    public int getScale() {
        return scale;
    }

    /**
     * Gets the display value in the default locale.
     */
    @Override
    public String toString() {
        return toString(Locale.getDefault());
    }

    /**
     * Gets the display value in the provided locale.
     */
    public String toString(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "Currency."+name()+".toString");
    }
}
