package lt.node.scandra.pirkimai.util

/**
 * Created by IntelliJ IDEA.
 * User: padargas
 * Date: 2/1/12
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */

import scala.xml._
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import android.widget.Toast
import android.content.ContextWrapper._
import android.content.{Context, Intent}

import transform.{RuleTransformer, RewriteRule}
import android.content.res.Resources
import java.io.{InputStream, File}
import lt.node.scandra.pirkimai.R
import android.app.{Activity, Application}
import android.app.Activity._

trait OrderPurchase {
  /*
order XML struktūra:
atributai:
   status     - pirkimo statusas              2: dar nepirkta, 1: dalinai nupirkta: 0: nupirkta
   matter     - prekė:                        {matter}[: {name}]
   amount     - kiekis:                       {NUMBER} {measure}
   rate       - rūšis:                        kaip 'goto_things'
   fatness    - riebumas:                     kaip 'goto_things'
   vol        - alkoholio %:                  kaip 'goto_things'
   kind       - kažkokia klasifikacija:       aaa bbb xxx zzz
   note       - prekės apibūdinimas:          free-text
//<order created="2012-01-26 22:46">
//  <t matter="pienas" amount="1 l" fatness="2.5%">2</t>
//  <t matter="jogurtas: Vaisinis" amount="0.5 l">2</t>
//  <t matter="bulvės: Prūsinės" amount="1 kg" rate="1r">2</t>
//  <t matter="žirniai: Galinta" amount="400 g" kind="skaldyti">2</t>
//</order>
<order created="2012-01-26 22:46">
<t  s="2" matter="pienas" amount="1 l" fatness="2.5%" />
<t  s="2" matter="jogurtas: Vaisinis" amount="0.5 l" />
<t  s="2" matter="bulvės: Prūsinės" amount="1 kg" rate="1r" />
<t  s="2" matter="žirniai: Galinta" amount="400 g" kind="skaldyti"/>
</order>
  */

  // ------------------------------------------------------
  final val TAG = "______"  //Pirkimai"
  final val STATUSPREFIX = "~=s=>"
  // ------------------------------------------------------


  def getStatus(xmlStr: String, itemStr: String): Int = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    (xmla \ "t").toList find (elem => /*purchaseItem2String*/orderItem2Str(elem) == itemStr) match {
      case Some(x) => (x \ "@s").toString.toInt
      case None => -1
    }
  }

  // http://www.ibm.com/developerworks/library/x-scalaxml/
  def orderItemsAll(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList} yield orderItem2Str(elem, true)/*purchaseItem2Str(elem, true)*//*orderItemXml2Txt(elem.toString)*/
  }

  /*def orderItems(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList if (elem \ "@s").toString() == "2"} yield purchaseItem2String(elem)
  }*/
  /*def orderItemsDone(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList if (elem \ "@s").toString() == "0"} yield purchaseItem2String(elem)
  }*/
  /*def orderItemsPartiallyDone(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList if (elem \ "@s").toString() == "1"} yield purchaseItem2String(elem)
  }*/

//  def purchaseItem2String(elem: Node): String = purchaseItem2Str(elem, false)

//  private def purchaseItem2Str(elem: Node, showStatus: Boolean): String = {
//    elem \ "@matter" + "  " + elem \ "@amount" +
//      attribExtractor(elem, "rate") +
//      attribExtractor(elem, "fatness") +
//      attribExtractor(elem, "vol") +
//      attribExtractor(elem, "kind") +
//      attribExtractor(elem, "note") +
//      (if (showStatus) "  " + this.STATUSPREFIX + elem \ "@s" else "")
//  }

  def orderItem2Str(elem: Node): String = orderItem2Str(elem, false)

  def orderItem2Str(elem: Node, showStatus: Boolean): String = {
    "" + elem \ "@matter" +
      ((elem \ "@name") match {
        case ns if ns.length == 0 => "  "
        case ns => ": " + ns + "  "
      }) +
      ((elem \ "@qty") match {
        case ns if ns.length == 0 => "  0 "
        case ns => "  " + ns + " "
      }) + elem \ "@measure" +
      attribExtractor(elem, "rate") +
      attribExtractor(elem, "fatness") +
      attribExtractor(elem, "vol") +
      attribExtractor(elem, "kind") +
      attribExtractor(elem, "note") +
      (if (showStatus) "  " + this.STATUSPREFIX + elem \ "@s" else "")
  }

  def templateItem2Str(elem: Node/*, showStatus: Boolean*/): String = {
    "" + elem \ "@matter" +
//      ((elem \ "@name") match {
//        case ns if ns.length == 0 => "  "
//        case ns => ": " + ns + "  "
//      }) +
//      ((elem \ "@qty") match {
//        case ns if ns.length == 0 => "  0 "
//        case ns => "  " + ns + " "
//      }) + elem \ "@measure" +
//      attribExtractor(elem, "rate") +
//      attribExtractor(elem, "fatness") +
//      attribExtractor(elem, "vol") +
//      attribExtractor(elem, "kind") +
//      attribExtractor(elem, "note") +
//      (if (showStatus) "  " + this.STATUSPREFIX + elem \ "@s" else ""
        ""
  }

  def attribExtractor(elem: Node, attrib: String): String =
    elem \ ("@" + attrib) match {
      case z if z.text == "" => ("");
      case z => ("  " + z.text)
    }

  def keyOfPurchaseItem(purchaseItemString: String): String = keyOfListViewItem(purchaseItemString)

  def keyOfListViewItem(itemString: String): String = itemString split " " mkString

  def orderItemXml2Txt(orderItemXmlStr: String): String = {
    val elem: Elem = XML.loadString(orderItemXmlStr)
    (elem \ "@measure") match {
      case ns if ns.length == 0 =>
        /*purchaseItem2String(elem)*/ "!!! error klaida ??? "
      case ns =>
        orderItem2Str(elem, false)
    }
  }

  /*def orderItemXml2Txt(orderItemXml: Elem): String = {
    val elem: Elem = orderItemXml
    (elem\"@measure") match {
      case ns if ns.length == 0 =>
        purchaseItem2String(elem)
      case ns =>
        orderItem2Str(elem, false)
    }
    //""
  }*/

  def preorderItems(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList} yield orderItem2Str(elem)
  }

  def templateItems(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList} yield templateItem2Str(elem)
  }

  def templateItems(xmlStr: String, groupId: String): List[String] =
    templateItems(XML.loadString(xmlStr), groupId)

  def templateItems(xml: NodeSeq, groupId: String): List[String] =
    for {elem <- (xml \ "t").toList.filter((a => ((a\"@g").toString == groupId)))} yield templateItem2Str(elem)


  //  // http://stackoverflow.com/questions/2569580/how-to-change-attribute-on-scala-xml-element ... ... ...
  //  implicit def addGoodCopyToAttribute(attr: Attribute) = new {
  //    def goodCopy(key: String = attr.key, value: Any = attr.value): Attribute =
  //      Attribute(attr.pre, key, Text(value.toString), attr.next)
  //  }
  //  implicit def iterableToMetaData(items: Iterable[MetaData]): MetaData = {
  //    items match {
  //      case Nil => Null
  //      case head :: tail => head.copy(next = iterableToMetaData(tail))
  //    }
  //  }
  //  def changeStatusInternal(e: Elem, delta: Int): Elem = {
  //    e match {
  //      case elem: Elem => elem.copy(attributes =
  //        //case elem: Elem =>
  //        //  val elemAttrs = elem.attributes/*.toList*/
  //        //  println (elemAttrs.toString())
  //        //  println (elem.attribute("s").toString())
  //        //  for (attr <- elemAttrs) println(attr.toString)
  //        for (attr <- elem.attributes) yield attr match {
  //          case attr@Attribute("s", _, _) =>
  //            attr.goodCopy(value = attr.value.text.toInt + delta)
  //          case other => other
  //        }
  //      )
  //      case _ => e
  //    }
  //  }

  /*def setStatusDone(purchase: Node, itemKey: String): Elem = {
    val temp = decreaseStatus(purchase, itemKey)
    decreaseStatus(temp.asInstanceOf[Node], itemKey)
  }*/

  /*def setStatusPartiallyDone(purchase: Node, itemKey: String): Elem = {
    decreaseStatus(purchase, itemKey)
  }*/

  /*def increaseStatus(purchase: Node, itemKey: String): Elem = {
    changeStatus(purchase, itemKey, +1)
  }*/

  /*def decreaseStatus(purchase: Node, itemKey: String): Elem = {
    changeStatus(purchase, itemKey, -1)
  }*/

  /*def changeStatus(purchase: Node, itemKey: String, delta: Int): Elem = {
    val newPurchase: List[Node] =
      for {elem <- (purchase \ "t").toList} yield {
        val key = keyOfPurchaseItem(purchaseItem2String(elem))
        elem match {
          case e if itemKey == key =>
            /*//println("==>"+changeStatusDecrease(e.asInstanceOf[Elem]).toString );
            changeStatusDecrease(e.asInstanceOf[Elem])*/
            println("==>"+changeStatusInternal(e.asInstanceOf[Elem], delta).toString );
            changeStatusInternal(e.asInstanceOf[Elem], delta)
          case e =>
            e
        }
      }
      <order created={purchase\"@created"}>{newPurchase}</order>
  }*/

  /*def changeStatusIncrease(e: Elem): Elem = {
    changeStatusInternal(e, +1)
  }
  def changeStatusDecrease(e: Elem): Elem = {
    changeStatusInternal(e, -1)
  }*/

  /*def changeStatusInternal(e: Elem, delta: Int): Elem = {
    e match {
      case elem: Elem => elem.copy(attributes =
        //case elem: Elem =>
        //  val elemAttrs = elem.attributes/*.toList*/
        //  println (elemAttrs.toString())
        //  println (elem.attribute("s").toString())
        //  for (attr <- elemAttrs) println(attr.toString)
        for (attr <- elem.attributes) yield attr match {
          case attr@Attribute("s", _, _) =>
            attr.goodCopy(value = attr.value.text.toInt + delta)
          case other => other
        }
      )
      case _ => e
    }
  }
*/
  // ==========================================================================
  // http://stackoverflow.com/questions/2569580/how-to-change-attribute-on-scala-xml-element
  // first solution
  case class GenAttr(pre: Option[String], key: String, value: Seq[Node], next: MetaData) {
    def toMetaData = Attribute(pre, key, value, next)
  }

  def decomposeMetaData(m: MetaData): Option[GenAttr] = m match {
    case Null => None
    case PrefixedAttribute(pre, key, value, next) =>
      Some(GenAttr(Some(pre), key, value, next))
    case UnprefixedAttribute(key, value, next) =>
      Some(GenAttr(None, key, value, next))
  }

  def unchainMetaData(m: MetaData): Iterable[GenAttr] =
    m flatMap (decomposeMetaData)

  def chainMetaData(l: Iterable[GenAttr]): MetaData = l match {
    case Nil => Null
    case head :: tail => head.copy(next = chainMetaData(tail)).toMetaData
  }

  def mapMetaData(m: MetaData)(f: GenAttr => GenAttr): MetaData =
    chainMetaData(unchainMetaData(m).map(f))

  def transfm(n: Node)(stat: Seq[Node]): Seq[Node] = (n match {
    case e: Elem =>
      //Log.v(this.TAG + " transfm Done--", "e: Elem " + e.toString)
      e.copy(attributes = mapMetaData(e.attributes) {
        case g@GenAttr(_, key, /*Text(v)*/ _, _) if key == "s" =>
          val etemp = g.copy(value = stat)
          //Log.v(this.TAG + " transfm", "case g@GenAttr " + etemp.toString)
          etemp
        case other =>
          //Log.v(this.TAG + " transfm", "case other " + other.toString)
          other
      })
    case other =>
      //Log.v(this.TAG + " transfm", "case OTHER " + other.toString)
      other
  }).toSeq

  //  val rrDone = new RewriteRule {
  //    //override def transform(n: Node): Seq[Node] = transfm(n)(Text("0"))
  //    override def transform(n: Node)/*(stat: Seq[Node])*/: Seq[Node] = (n match {
  //      case e: Elem =>
  //        Log.v(this.TAG + " Done--", "e: Elem " + e.toString)
  //        e.copy(attributes = mapMetaData(e.attributes) {
  //          case g@GenAttr(_, key, /*Text(v)*/_, _) if key == "s" =>
  //            val etemp = g.copy(value = Text("0"))
  //            Log.v(this.TAG + " Done--", "case g@GenAttr " + etemp.toString)
  //            etemp
  //          case other =>
  //            Log.v(this.TAG + " Done--", "case other " + other.toString)
  //            other
  //        })
  //      case other =>
  //        Log.v(this.TAG + " Done--", "case OTHER " + other.toString)
  //        other
  //    }).toSeq
  //  }
  //  val rtDone = new RuleTransformer(rrDone)

  //  val rrPartiallyDone = new RewriteRule {
  //    override def transform(n: Node): Seq[Node] = transfm(n)(Text("1"))
  //  }
  //  val rtPartiallyDone = new RuleTransformer(rrPartiallyDone)


  def updateStatus(purchase: Node, itemKey: String, newStatus: Int): Elem = {
    val newPurchase: scala.collection.immutable.Seq[scala.collection.Seq[scala.xml.Node]] =
      for {elem <- (purchase \ "t")} yield {
        Log.v(this.TAG + " updateStatus", "elem=" + elem.toString)
        val key = keyOfPurchaseItem(/*purchaseItem2String*/ orderItemXml2Txt(elem.toString))
        Log.v(this.TAG + " updateStatus", "key=" + key + ";  itemKey=|" + itemKey + "|")
        elem match {
          case e if itemKey == key =>
            Log.v(this.TAG + " OrderPurchase updateStatus", "--updateStatus-- " + e.toString)
            newStatus match {
              case 0 =>
                transfm(e.asInstanceOf[Node])(Text("0"))
              //Log.v(this.TAG + " updateStatus case 0 ", "transfm(n)(Text(\"0\")) " + transfm(e.asInstanceOf[Node])(Text("0")).toString())
              case 1 =>
                transfm(e.asInstanceOf[Node])(Text("1"))
              case 2 =>
                transfm(e.asInstanceOf[Node])(Text("2"))
              case _ => e
            }
          case e => e
        }
      }
    <order created={purchase \ "@created"}
           updated={new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())}>
      {newPurchase}
    </order>
  }


  def addChild(parent: Node, newChild: Node): Option[Node] = parent match {
    case Elem(prefix, label, attribs, scope, child@_*) =>
      Some(Elem(prefix, label, attribs, scope, child ++ newChild: _*))
    case _ =>
      None
  }

  //-> http://stackoverflow.com/questions/1477215/find-all-nodes-that-have-an-attribute-that-matches-a-certain-value-with-scala
  def getByAtt(e: Elem, att: String, value: String): NodeSeq = {
    def filterAtr(node: Node, att: String, value: String) = (node \ ("@" + att)).text == value
    e \\ "_" filter {
      n => filterAtr(n, att, value)
    }
  }

  //  def thingItemByMatter(xmlStr: String, matter: String): List[String] = {
  //    val xmla: NodeSeq = XML.loadString(xmlStr)
  //    val tList: List[Node] = for {elem <- (xmla \ "t").toList } yield elem
  //    tList find (n =>  (n \ "@matter")  )
  //  }

  def thingItems(xmlStr: String): List[String] = {
    def thingItem2Str(elem: Node): String = elem \ "@matter" + "  [ " + elem \ "@measure" + " ] "
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList} yield thingItem2Str(elem)
  }

  def thingItems(xmlStr: String, groupId: String): List[String] = {
    def thingItem2Str(elem: Node): String = elem \ "@matter" + "  [ " + elem \ "@measure" + " ] "
    val xmla: NodeSeq = XML.loadString(xmlStr)
    for {elem <- (xmla \ "t").toList.filter((a => ((a\"@g").toString == groupId)))} yield thingItem2Str(elem)
  }

  //-> http://www.scalaclass.com/node/62  [Making it easier to add attributes to an XML element]
  implicit def pimp(elem: Elem) = new {
    def %(attrs: Map[String, String]) = {
      val seq = for ((n, v) <- attrs) yield new UnprefixedAttribute(n, v, Null)
      (elem /: seq)(_ % _)
    }
  }

  def temp2order(application: Context, dir: File) {
    Log.v(this.TAG + " trait", " temp2order ...")
    //val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    new File(dir, "orderxml.txt").exists() match {
      case true =>
        new File(dir, "orderxml.txt").renameTo(new File(dir,
          "orderxml_" + (new SimpleDateFormat("yyyyMMddHHmm").format(new Date())) + ".txt"))
      case _ =>
    }
    FileUtil.copyFile(new File(dir, "tempxml.txt"), new File(dir, "orderxml.txt"))
    new File(dir, "tempxml.txt").delete
    Toast.makeText(application, "Pirkinių sąrašas jau suformuotas", Toast.LENGTH_LONG).show()
    //startActivity(new Intent(this, classOf[Main]) /*.putExtra("createOrder", "yes")*/)
  }


  def removeOrderItem(nodeStr: String, itemStr: String): Node /*Seq[Node]*/ = {
    //-> http://stackoverflow.com/questions/3890457/removing-nodes-from-xml
    val node: Node = XML.loadString(nodeStr)
    val removeIt: RewriteRule = new RewriteRule {
      override def transform(n: Node): NodeSeq = n match {
        case e: Elem if (orderItem2Str(e) split " " mkString) == (itemStr split " " mkString) => NodeSeq.Empty
        case e => e
      }
    }
    new RuleTransformer(removeIt).transform(node)(0)
  }


  def removeTemplateByMatter(thingsStr: String, matter: String): Node = {
    //-> http://stackoverflow.com/questions/3890457/removing-nodes-from-xml
    val node: Node = XML.loadString(thingsStr)
    val removeIt: RewriteRule = new RewriteRule {
      override def transform(n: Node): NodeSeq = n match {
        case e: Elem if (e\"@matter").toString == matter => NodeSeq.Empty
        case e => e
      }
    }
    new RuleTransformer(removeIt).transform(node)(0)
  }


  def changeElemAttrib(elemStr: String, attribName: String, attribValue: String): String  = {
//    val elem: Elem =  removeElemAttrib(elemStr, attribName)
//    val thingString: String = (XML.loadString(getIntent.getStringExtra("thingString")) %
//      new UnprefixedAttribute(getIntent.getStringExtra("attrib"), attribValuesStr, Null)).toString
    (removeElemAttrib(elemStr, attribName) % new UnprefixedAttribute(attribName, attribValue, Null)).toString

  }


  def removeElemAttrib(elemStr: String, attribName: String): Elem  = {
    val elem: Elem = XML.loadString(elemStr)
    elem.attribute(attribName) match {
      case Some(x) => elem.copy(attributes = elem.attributes.remove(attribName))
      case _ => elem
    }
  }


  def removeOrderItemAttrib(elemStr: String, attribName: String): Elem  = {
    /*val elem: Elem = XML.loadString(elemStr)
    elem.attribute(attribName) match {
      case Some(x) =>
        elem.copy(attributes = elem.attributes.remove(attribName))
      case _ =>
        elem
    }*/
    removeElemAttrib(elemStr, attribName)
  }


  def getOrderItemByItemString(orderStr: String, itemKey: String): Option[Node] = {
    val order: Node = XML.loadString(orderStr)
    //    val nodeSeq: scala.collection.immutable.Seq[scala.collection.Seq[scala.xml.Node]] = (order\"t")
    val nodeSeq: NodeSeq = (order \ "t")
    /*for {elem <- (order \ "t")} yield {
      Log.v(this.TAG + " getOrderItemByItemString", "elem = " + elem.toString)
      val key = keyOfListViewItem(orderItemXml2Txt(elem.toString))
      elem match {
        case e if itemKey == key => Some(e)
        case e => None
      }
    }*/
    //    nodeSeq find (elem =>  elem match {
    //      case e if itemKey == keyOfListViewItem(orderItemXml2Txt(elem.toString)) => Some(e)
    //      case e => None
    //    })
    //    nodeSeq find {elem: Node => val xxx = orderItemXml2Txt(elem.toString);
    //    val key = keyOfListViewItem(xxx);
    //    itemKey == key
    //      }
    nodeSeq find {
      node: Node => ((itemKey split " " mkString) == keyOfListViewItem(orderItemXml2Txt(node.toString)))
    }
  }


  def getXmlAsStr(rRawRes: Int, rawFileNameExt: String, activity: Activity): String = {
    val dir = FileUtil.getExternalFilesDirAllApiLevels(activity.getPackageName);
    val file = new File(dir, rawFileNameExt)
    val xmlAsStr: String = file.exists() match {
      case false => // installation time case
        Log.v(TAG + " Trait.getXmlAsStr file", " case false ...")
        val resources: Resources = activity.getResources
        val srcStream: InputStream = resources openRawResource rRawRes
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
        FileUtil.writeStringAsFile(srcString, file)
        FileUtil.readFileAsString(file)
      case _ =>  //
        Log.v(TAG + " Trait.getXmlAsStr  file", " case _ ...")
        FileUtil.readFileAsString(file)
    }
    xmlAsStr
  }


  def getXmlAsStr(rRawRes: Int, rawFileNameExt: String, anEextraName: String, activity: Activity): String = {
    activity.getIntent.hasExtra(anEextraName) match {
      case false =>
        //Log.v(TAG + "  Trait.getXmlAsStr anEextraName", "case false ...")
        getXmlAsStr(rRawRes/*: Int*/, rawFileNameExt/*: String*/, activity/*: Activity*/)
      case true =>
        //Log.v(TAG + " Trait.getXmlAsStr anEextraName", "case true ...")
        activity.getIntent.getStringExtra(anEextraName)
    }
  }
  
  
    def getTemplateGroups(rRawRes: Int, rawFileNameExt: String, anEextraName: String, activity: Activity): Array[String] = {
      val xmlStr: String = getXmlAsStr(rRawRes, rawFileNameExt, anEextraName, activity)
      val meta: Node = XML.loadString(xmlStr)
      //(meta \ "mandatProps" \ "p").filter(_ \ "@n" exists (_.text == "group")).map(a=>(a\"v").text).toArray[String]
      //(((meta \ "mandatProps" \ "p").filter(_ \ "@n" exists (_.text == "group"))(0))\\"v").toList.map(a=>a.text).toArray[String]
      (((meta \ "groups")(0))\"v").toList.map(a=>a.text).toArray[String]

    }


    def getTemplateGroupIdByName(rRawRes: Int, rawFileNameExt: String, anEextraName: String,
                                 activity: Activity, groupName: String): String = {
      val xmlStr: String = getXmlAsStr(rRawRes, rawFileNameExt, anEextraName, activity)
      val meta: Node = XML.loadString(xmlStr)
//      (meta \ "mandatProps" \ "p").filter(_ \ "@n" exists (_.text == "group")) \ "v" find(_.text == groupName) match {
      ((meta \ "groups")(0)) \ "v" find(_.text == groupName) match {
        case Some(node) => (node \ "@k").text
        case _ => "0"
      }
    }


    def getThingByMatter(rRawRes: Int, rawFileNameExt: String, matter: String, activity: Activity): String = {
      val xmlStr: String = getXmlAsStr(rRawRes, rawFileNameExt, activity)
      val res0: Node = XML.loadString(xmlStr)
      (res0 \ "t") find((e: Node) =>(( e\"@matter")(0).text == matter)) match {
        case Some(node) => node.toString
        case _ => ""  // <t></t>.toString
      }
    }


  def getMandatPropValues(rRawRes: Int, rawFileNameExt: String, propName: String, activity: Activity): Array[String] = {
    val xmlStr: String = getXmlAsStr(rRawRes, rawFileNameExt, /*anEextraName,*/ activity)
    val res0: Node = XML.loadString(xmlStr)
    //((((res0 \ "mandatProps")(0))\"p").filter(_ \ "@n" exists (_.text == propName))(0)\"@values").toString.split("\\|")
    ((res0 \\ "p").filter(_ \ "@n" exists (_.text == propName))(0)\"@values").toString.split("\\|")
  }

  def getMandatPropValues(propName: String, activity: Activity): Array[String] = {
    val xmlStr: String = getXmlAsStr(R.raw.metadataxml, "metadataxml.txt", activity)
    val res0: Node = XML.loadString(xmlStr)
    //((((meta \ "mandatProps")(0))\"p").filter(_ \ "@n" exists (_.text == propName))(0)\"@values").toString.split("\\|")
    ((res0 \\ "p").filter(_ \ "@n" exists (_.text == propName))(0)\"@values").toString.split("\\|")
  }

  def getMeasures(rRawRes: Int, rawFileNameExt: String, /*anEextraName: String,*/ activity: Activity): Array[String] = {
    //val xmlStr: String = getXmlAsStr(rRawRes, rawFileNameExt, /*anEextraName,*/ activity)
    //val meta: Node = XML.loadString(xmlStr)
    //((((meta \ "mandatProps")(0))\"p").filter(_ \ "@n" exists (_.text == "measure"))(0)\"@values").toString.split("\\|")
    getMandatPropValues(rRawRes, rawFileNameExt, "measure", activity)
  }

  def getQtys(/*rRawRes: Int, rawFileNameExt: String, *//*anEextraName: String,*/ activity: Activity): Array[String] = {
    getMandatPropValues(/*rRawRes, rawFileNameExt, */"qty", activity)
  }

  def getXxxxs(anEextraName: String, activity: Activity): Array[String] = {
    getMandatPropValues(anEextraName, activity)
  }



}

