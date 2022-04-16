/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.i18n;

import com.aoapps.collections.AoCollections;
import com.aoapps.lang.i18n.Resources;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ISO 3166-1 alpha-2 country codes.
 * <p>
 * Source: <a href="https://wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements">https://wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements</a> on 2009-12-13
 * </p>
 * TODO: Update before next production release
 *
 * @author  AO Industries, Inc.
 */
public enum Country {

	AD(1974),
	AE(1974),
	AF(1974),
	AG(1974),
	AI(1983),
	AL(1974),
	AM(1992),
	AN(1974),
	AO(1974),
	AQ(1974),
	AR(1974),
	AS(1974),
	AT(1974),
	AU(
		1974,
		new String[] {
			"Capital Territory",
			"New South Wales",
			"Northern Territory",
			"Queensland",
			"South Australia",
			"Tasmania",
			"Victoria",
			"Western Australia"
		}
	),
	AW(1986),
	AX(2004),
	AZ(1992),
	BA(1992),
	BB(1974),
	BD(1974),
	BE(
		1974,
		new String[] {
			"Antwerpen",
			"Brabant Wallon",
			"Brussels",
			"Hainaut",
			"Liege",
			"Limburg",
			"Luxembourg",
			"Namur",
			"Oost-Vlaanderen",
			"Vlaams Brabant",
			"West-Vlaanderen"
		}
	),
	BF(1984),
	BG(1974),
	BH(1974),
	BI(1974),
	BJ(1977),
	BL(2007),
	BM(1974),
	BN(1974),
	BO(1974),
	BR(
		1974,
		new String[] {
			"Acre",
			"Alagoas",
			"Amapa",
			"Amazonas",
			"Bahia",
			"Ceara",
			"Distrito Federal",
			"Espirito Santo",
			"Goias",
			"Maranhao",
			"Mato Grosso",
			"Mato Grosso do Sul",
			"Minas Gerais",
			"Para",
			"Paraiba",
			"Parana",
			"Pernambuco",
			"Piaui",
			"Rio de Janeiro",
			"Rio Grande do Norte",
			"Rio Grande do Sul",
			"Rondonia",
			"Roraima",
			"Santa Catarina",
			"Sao Paulo",
			"Sergipe",
			"Tocantins"
		}
	),
	BS(1974),
	BT(1974),
	BV(1974),
	BW(1974),
	BY(1974),
	BZ(1974),
	CA(
		1974,
		new String[] {
			"Alberta",
			"British Columbia",
			"Manitoba",
			"New Brunswick",
			"Newfoundland",
			"Northwest Territories",
			"Nova Scotia",
			"Nunavut",
			"Ontario",
			"Prince Edward Island",
			"Quebec",
			"Saskatchewan",
			"Yukon Territory"
		}
	),
	CC(1974),
	CD(1997),
	CF(1974),
	CG(1974),
	CH(1974),
	CI(1974),
	CK(1974),
	CL(1974),
	CM(1974),
	CN(1974),
	CO(1974),
	CR(1974),
	CU(1974),
	CV(1974),
	CX(1974),
	CY(1974),
	CZ(1993),
	DE(1974),
	DJ(1977),
	DK(1974),
	DM(1974),
	DO(1974),
	DZ(1974),
	EC(1974),
	EE(1992),
	EG(1974),
	EH(1974),
	ER(1993),
	ES(1974),
	ET(1974),
	FI(1974),
	FJ(1974),
	FK(1974),
	FM(1986),
	FO(1974),
	FR(1974),
	GA(1974),
	GB(1974),
	GD(1974),
	GE(1992),
	GF(1974),
	GG(2006),
	GH(1974),
	GI(1974),
	GL(1974),
	GM(1974),
	GN(1974),
	GP(1974),
	GQ(1974),
	GR(1974),
	GS(1993),
	GT(1974),
	GU(1974),
	GW(1974),
	GY(1974),
	HK(1974),
	HM(1974),
	HN(1974),
	HR(1992),
	HT(1974),
	HU(1974),
	ID(1974),
	IE(1974),
	IL(1974),
	IM(2006),
	IN(
		1974,
		new String[] {
			"Andaman and Nicobar Islands",
			"Andhra Pradesh",
			"Arunachal Pradesh",
			"Assam",
			"Bihar",
			"Chandigarh",
			"Chhattisgarh",
			"Dadra and Nagar Haveli",
			"Daman and Diu",
			"Delhi",
			"Goa",
			"Gujarat",
			"Haryana",
			"Himachal Pradesh",
			"Jammu and Kashmir",
			"Jharkhand",
			"Karnataka",
			"Kerala",
			"Lakshadweep",
			"Madhya Pradesh",
			"Maharashtra",
			"Manipur",
			"Meghalaya",
			"Mizoram",
			"Nagaland",
			"Orissa",
			"Puducherry",
			"Punjab",
			"Rajasthan",
			"Sikkim",
			"Tamil Nadu",
			"Tripura",
			"Uttar Pradesh",
			"Uttarakhand",
			"West Bengal"
		}
	),
	IO(1974),
	IQ(1974),
	IR(1974),
	IS(1974),
	IT(1974),
	JE(2006),
	JM(1974),
	JO(1974),
	JP(
		1974,
		new String[] {
			"Aichi",
			"Akita",
			"Aomori",
			"Chiba",
			"Ehime",
			"Fukui",
			"Fukuoka",
			"Fukushima",
			"Gifu",
			"Gumma",
			"Hiroshima",
			"Hokkaido",
			"Hyogo",
			"Ibaraki",
			"Ishikawa",
			"Iwate",
			"Kagawa",
			"Kagoshima",
			"Kanagawa",
			"Kouchi",
			"Kumamoto",
			"Kyoto",
			"Mie",
			"Miyagi",
			"Miyazaki",
			"Nagano",
			"Nagasaki",
			"Nara",
			"Niigata",
			"Oita",
			"Okayama",
			"Okinawa",
			"Osaka",
			"Saga",
			"Saitama",
			"Shiga",
			"Shimane",
			"Shizuoka",
			"Tochigi",
			"Tokushima",
			"Tokyo",
			"Tottori",
			"Toyama",
			"Wakayama",
			"Yamagata",
			"Yamaguchi",
			"Yamanashi"
		}
	),
	KE(1974),
	KG(1992),
	KH(1974),
	KI(1979),
	KM(1974),
	KN(1974),
	KP(1974),
	KR(1974),
	KW(1974),
	KY(1974),
	KZ(1992),
	LA(1974),
	LB(1974),
	LC(1974),
	LI(1974),
	LK(1974),
	LR(1974),
	LS(1974),
	LT(1992),
	LU(1974),
	LV(1992),
	LY(1974),
	MA(1974),
	MC(1974),
	MD(1992),
	ME(2006),
	MF(2007),
	MG(1974),
	MH(1986),
	MK(1993),
	ML(1974),
	MM(1989),
	MN(1974),
	MO(1974),
	MP(1986),
	MQ(1974),
	MR(1974),
	MS(1974),
	MT(1974),
	MU(1974),
	MV(1974),
	MW(1974),
	MX(
		1974,
		new String[] {
			"Aguascalientes",
			"Baja California",
			"Baja California Sur",
			"Campeche",
			"Chiapas",
			"Chihuahua",
			"Coahuila",
			"Colima",
			"Distrito Federal",
			"Durango",
			"Guanajuato",
			"Guerrero",
			"Hidalgo",
			"Jalisco",
			"Mexico",
			"Michoacan",
			"Morelos",
			"Nayarit",
			"Nuevo Leon",
			"Oaxaca",
			"Puebla",
			"Queretaro",
			"Quintana Roo",
			"San Luis Potosi",
			"Sinaloa",
			"Sonora",
			"Tabasco",
			"Tamaulipas",
			"Tlaxcala",
			"Veracruz",
			"Yucatan",
			"Zacatecas"
		}
	),
	MY(1974),
	MZ(1974),
	NA(1974),
	NC(1974),
	NE(1974),
	NF(1974),
	NG(1974),
	NI(1974),
	NL(1974),
	NO(1974),
	NP(1974),
	NR(1974),
	NU(1974),
	NZ(1974),
	OM(1974),
	PA(1974),
	PE(1974),
	PF(1974),
	PG(1974),
	PH(1974),
	PK(1974),
	PL(1974),
	PM(1974),
	PN(1974),
	PR(1974),
	PS(1999),
	PT(1974),
	PW(1986),
	PY(1974),
	QA(1974),
	RE(1974),
	RO(1974),
	RS(2006),
	RU(1992),
	RW(1974),
	SA(1974),
	SB(1974),
	SC(1974),
	SD(1974),
	SE(1974),
	SG(1974),
	SH(1974),
	SI(1992),
	SJ(1974),
	SK(1993),
	SL(1974),
	SM(1974),
	SN(1974),
	SO(1974),
	SR(1974),
	ST(1974),
	SV(1974),
	SY(1974),
	SZ(1974),
	TC(1974),
	TD(1974),
	TF(1979),
	TG(1974),
	TH(1974),
	TJ(1992),
	TK(1974),
	TL(2002),
	TM(1992),
	TN(1974),
	TO(1974),
	TR(1974),
	TT(1974),
	TV(1979),
	TW(1974),
	TZ(1974),
	UA(1974),
	UG(1974),
	UM(1986),
	US(
		1974,
		new String[] {
			"Alabama",
			"Alaska",
			"American Samoa",
			"Arizona",
			"Arkansas",
			"Armed Forces America",
			"Armed Forces Other Areas",
			"Armed Forces Pacific",
			"California",
			"Colorado",
			"Connecticut",
			"Delaware",
			"District of Columbia",
			"Federated States of Micronesia",
			"Florida",
			"Georgia",
			"Guam",
			"Hawaii",
			"Idaho",
			"Illinois",
			"Indiana",
			"Iowa",
			"Kansas",
			"Kentucky",
			"Louisiana",
			"Maine",
			"Marshall Islands",
			"Maryland",
			"Massachusetts",
			"Michigan",
			"Minnesota",
			"Mississippi",
			"Missouri",
			"Montana",
			"Nebraska",
			"Nevada",
			"New Hampshire",
			"New Jersey",
			"New Mexico",
			"New York",
			"North Carolina",
			"North Dakota",
			"Northern Mariana Islands",
			"Ohio",
			"Oklahoma",
			"Oregon",
			"Palau",
			"Pennsylvania",
			"Puerto Rico",
			"Rhode Island",
			"South Carolina",
			"South Dakota",
			"Tennessee",
			"Texas",
			"Virgin Islands",
			"Utah",
			"Vermont",
			"Virginia",
			"Washington",
			"West Virginia",
			"Wisconsin",
			"Wyoming"
		}
	),
	UY(1974),
	UZ(1992),
	VA(1974),
	VC(1974),
	VE(1974),
	VG(1974),
	VI(1974),
	VN(1974),
	VU(1980),
	WF(1974),
	WS(1974),
	YE(1974),
	YT(1993),
	ZA(1974),
	ZM(1974),
	ZW(1980);

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Country.class);

	private final int since;
	private final List<String> states;

	private Country(int since) {
		this.since = since;
		this.states = null;
	}

	private Country(int since, String[] states) {
		this.since = since;
		this.states = AoCollections.optimalUnmodifiableList(Arrays.asList(states));
	}

	/**
	 * Gets the year this country code was created.
	 */
	public int getSince() {
		return since;
	}

	@Override
	public String toString() {
		return RESOURCES.getMessage(name() + ".toString");
	}

	/**
	 * Gets the unmodifiable list of states or <code>null</code> if unknown.
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<String> getStates() {
		return states;
	}
}
