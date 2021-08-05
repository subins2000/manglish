package subins2000.manglish;

import org.junit.Test;

import static org.junit.Assert.*;

public class ml2enTest {

    @Test
    public void letters() {
        ml2en m = new ml2en();

        assertEquals("Ka", m.convert("ക"));
        assertEquals("Sa", m.convert("സ"));
    }

    @Test
    public void words() {
        ml2en m = new ml2en();

        assertEquals("Thvakku", m.convert("ത്വക്ക്"));
        assertEquals("Kaakka", m.convert("കാക്ക"));
        assertEquals("Laksham", m.convert("ലക്ഷം"));
        assertEquals("Valiya", m.convert("വലിയ"));
        assertEquals("Malayaalam", m.convert("മലയാളം"));
        assertEquals("Thvakku", m.convert("ത്വക്ക്"));
        assertEquals("Pankaayam", m.convert("പങ്കായം"));

        assertEquals("Hello", m.convert("hello"));
    }

    @Test
    public void sentences() {
        ml2en m = new ml2en();

        assertEquals("Oru phranchu gaNithashaasthrajnjanum bhauthikashaasthrajnjanum raashTreeyakkaaranumaayirunnu lasaare kaarnoTTu.",
                m.convert("ഒരു ഫ്രഞ്ച് ഗണിതശാസ്ത്രജ്ഞനും ഭൗതികശാസ്ത്രജ്ഞനും രാഷ്ട്രീയക്കാരനുമായിരുന്നു ലസാരെ കാർനോട്ട്."));
        assertEquals("2019 epril 17-nu malayaalam vikkipeediyayile lekhanangaluTe eNNam 63,000 pinniTTu.",
                m.convert("2019 ഏപ്രിൽ 17-ന് മലയാളം വിക്കിപീഡിയയിലെ ലേഖനങ്ങളുടെ എണ്ണം 63,000 പിന്നിട്ടു."));
        assertEquals("1962 - phraansinethireyulla aljeeriyayuTe svaathanthuryasamaram avasaanicchu.",
                m.convert("1962 - ഫ്രാൻസിനെതിരെയുള്ള അൾജീരിയയുടെ സ്വാതന്ത്ര്യസമരം അവസാനിച്ചു."));
    }

    @Test
    public void paragaraphs() {
        ml2en m = new ml2en();

        assertEquals("Naasi jarmani undaakkiya joothaviruddha niyamangalaaNu nyoorambargu niyamangal. NaasippaarTTiyuTe vaarshikaraaliyil 1935 septhambar 15-naaNu ithu puratthirakkiyathu. Randu niyamangalil aadyatthethil jarman rakthavum jarman abhimaanavum samrakshikkunnathinaayi undaakkiya niyamangalil joothanmaarum jarmankaarumaayulla vivaahavum lymgikabandhangalum niyamaviruddhamaakkunnu. KooTaathe 45 vayasil thaazheyulla jarman sthreekale joothabhavanangalil veeTTujolikal cheyyunnathilninnum thaTayunnu. Randaamatthe jarman paurathvaniyamatthil jarmano allenkil athumaaya bandhamulla rakthamullavarkkumaathramaaNu paurathvatthinulla avakaasham. Sheshicchavar verum adheenathayil ulla prajakal maathram. Navambar 14-nu aarokkeyaaNu joothanmaar enna kaaryavum kooTTicchertthu pittennumuthal ithu niyamamaayi. Navambar 26-nu jipsikaleyum aaphrikkan vamshajaraaya jarmankaareyum paurathvatthil ninnum ozhivaakki niyamam konduvannu. AnthaaraashTranaTapaTikal bhayannu 1936-le myooNiku olimpiksu kazhiyunnathuvare shikshaanaTapaTikal onnum eTutthilla. ",
                m.convert("നാസി ജർമനി ഉണ്ടാക്കിയ ജൂതവിരുദ്ധ നിയമങ്ങളാണ് ന്യൂറംബർഗ് നിയമങ്ങൾ. നാസിപ്പാർട്ടിയുടെ വാർഷികറാലിയിൽ 1935 സെപ്തംബർ 15-നാണ് ഇത് പുറത്തിറക്കിയത്. രണ്ടു നിയമങ്ങളിൽ ആദ്യത്തേതിൽ ജർമൻ രക്തവും ജർമൻ അഭിമാനവും സംരക്ഷിക്കുന്നതിനായി ഉണ്ടാക്കിയ നിയമങ്ങളിൽ ജൂതന്മാരും ജർമൻകാരുമായുള്ള വിവാഹവും ലൈംഗികബന്ധങ്ങളും നിയമവിരുദ്ധമാക്കുന്നു. കൂടാതെ 45 വയസ്സിൽ താഴെയുള്ള ജർമൻ സ്ത്രീകളെ ജൂതഭവനങ്ങളിൽ വീട്ടുജോലികൾ ചെയ്യുന്നതിൽനിന്നും തടയുന്നു. രണ്ടാമത്തെ ജർമൻ പൗരത്വനിയമത്തിൽ ജർമനോ അല്ലെങ്കിൽ അതുമായ ബന്ധമുള്ള രക്തമുള്ളവർക്കുമാത്രമാണ് പൗരത്വത്തിനുള്ള അവകാശം. ശേഷിച്ചവർ വെറും അധീനതയിൽ ഉള്ള പ്രജകൾ മാത്രം. നവംബർ 14-ന് ആരൊക്കെയാണ് ജൂതന്മാർ എന്ന കാര്യവും കൂട്ടിച്ചേർത്ത് പിറ്റേന്നുമുതൽ ഇതു നിയമമായി. നവംബർ 26-ന് ജിപ്സികളെയും ആഫ്രിക്കൻ വംശജരായ ജർമൻകാരെയും പൗരത്വത്തിൽ നിന്നും ഒഴിവാക്കി നിയമം കൊണ്ടുവന്നു. അന്താരാഷ്ട്രനടപടികൾ ഭയന്ന് 1936-ലെ മ്യൂണിക് ഒളിമ്പിക്സ് കഴിയുന്നതുവരെ ശിക്ഷാനടപടികൾ ഒന്നും എടുത്തില്ല. "));
        assertEquals("Vyaazhatthinte kaanthikakshethram sauravaathatthe cherukkunna mekhalayaaNu vyaazhatthinte kaanthamandalam. Sooryanilekkulla dishayil ethaandu ezhupathu laksham kilomeettarum vipareetha dishayil shaniyuTe parikramaNapatham vareyum ithu vyaapicchukiTakkunnu. Saurayoothatthile grahangaluTe kaanthamandalangalil vacchu ettavum shakthiyeriyathaaNu vyaazhatthintethu. Sauramandalam kazhinjaal saurayoothatthile ettavum valiya ghaTanayum ithuthanne. BhoomiyuTe kaanthamandalatthekkaal veethiyeriyathum parannathumaaya vyaazhatthinte kaanthamandalatthinte shakthi bhoomiyooTethinte patthiraTTiyolavum vyaaptham 18000 iraTTiyolavumaaN",
                m.convert("വ്യാഴത്തിന്റെ കാന്തികക്ഷേത്രം സൗരവാതത്തെ ചെറുക്കുന്ന മേഖലയാണ് വ്യാഴത്തിന്റെ കാന്തമണ്ഡലം. സൂര്യനിലേക്കുള്ള ദിശയിൽ ഏതാണ്ട് എഴുപത് ലക്ഷം കിലോമീറ്ററും വിപരീത ദിശയിൽ ശനിയുടെ പരിക്രമണപഥം വരെയും ഇത് വ്യാപിച്ചുകിടക്കുന്നു. സൗരയൂഥത്തിലെ ഗ്രഹങ്ങളുടെ കാന്തമണ്ഡലങ്ങളിൽ വച്ച് ഏറ്റവും ശക്തിയേറിയതാണ് വ്യാഴത്തിന്റേത്. സൗരമണ്ഡലം കഴിഞ്ഞാൽ സൗരയൂഥത്തിലെ ഏറ്റവും വലിയ ഘടനയും ഇതുതന്നെ. ഭൂമിയുടെ കാന്തമണ്ഡലത്തെക്കാൾ വീതിയേറിയതും പരന്നതുമായ വ്യാഴത്തിന്റെ കാന്തമണ്ഡലത്തിന്റെ ശക്തി ഭൂമിയൂടേതിന്റെ പത്തിരട്ടിയോളവും വ്യാപ്തം 18000 ഇരട്ടിയോളവുമാണ്"));

        assertEquals("Angane avaru chennu nammaTe veeTTukaaroTu avaruTe vakayile kunjammeTe aniyattheeTe mole patti parayum. NammaTe veeTTukaaru nammale aavunnathra veruppikkum. Angane veruppicchu veruppicchu veruppinte angetthalaykkaletthumpo nammalu aliyanteyo suhrutthukkaluTe kooTeyo peNNin്re veeTTi pokaan sammathikkum. KeTTaan muTTi nikkunnavanmaaru chaaTikkeri o yaahu enna nilapaaTum eTukkum. PranayabandhatthilakappeTTa chilavanmaaru samayamaayiTTillennu parayum, chilaru poyu kandekkaam, kayyilullathinekkaa ToppaaNu kaaNaan pokunnathenkilo ennu karuthi oralpam asahyatha abhinayikkum.",
                m.convert("അങ്ങനെ അവര് ചെന്ന് നമ്മടെ വീട്ടുകാരോട് അവരുടെ വകയിലെ കുഞ്ഞമ്മേടെ അനിയത്തീടെ മോളെ പറ്റി പറയും. നമ്മടെ വീട്ടുകാര് നമ്മളെ ആവുന്നത്ര വെറുപ്പിക്കും. അങ്ങനെ വെറുപ്പിച്ചു വെറുപ്പിച്ചു വെറുപ്പിന്റെ അങ്ങേത്തലയ്ക്കലെത്തുമ്പൊ നമ്മള് അളിയന്റെയൊ സുഹൃത്തുക്കളുടെ കൂടെയോ പെണ്ണിൻ്റെ വീട്ടി പോകാൻ സമ്മതിക്കും. കെട്ടാൻ മുട്ടി നിക്കുന്നവന്മാര് ചാടിക്കേറി ഓ യാഹ് എന്ന നിലപാടും എടുക്കും. പ്രണയബന്ധത്തിലകപ്പെട്ട ചിലവന്മാര് സമയമായിട്ടില്ലെന്ന് പറയും, ചിലര് പോയ് കണ്ടേക്കാം, കയ്യിലുള്ളതിനേക്കാ ടോപ്പാണ് കാണാൻ പോകുന്നതെങ്കിലോ എന്ന് കരുതി ഒരല്പം അസഹ്യത അഭിനയിക്കും."));

        assertEquals("Algorithm - kanakkuvazhi Kernel - anthasaaram Interface - samgamasthalam Graph - rekhaaroopam Network - shrumkhala Prototype - moolaroopam Variable - parivartthithavasthu Constant - shaashvatham Transaction - vyavahaaram\n" +
                "\n" +
                "Polymorphous - naanaaroopatthilulla\n" +
                "\n" +
                "The words you mentioned is not exclusively owned by Computer science. They were existing English words that got adapted to express ideas in Computer Science.",
                m.convert("Algorithm - കണക്കുവഴി Kernel - അന്തസ്സാരം Interface - സംഗമസ്ഥലം Graph - രേഖാരൂപം Network - ശൃംഖല Prototype - മൂലരൂപം Variable - പരിവർത്തിതവസ്തു Constant - ശാശ്വതം Transaction - വ്യവഹാരം\n" +
                "\n" +
                "Polymorphous - നാനാരൂപത്തിലുള്ള\n" +
                "\n" +
                "The words you mentioned is not exclusively owned by Computer science. They were existing English words that got adapted to express ideas in Computer Science."));
    }
}
