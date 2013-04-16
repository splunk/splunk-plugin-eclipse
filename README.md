# Splunk plug-in for Eclipse

#### Version 0.1

The Splunk plug-in for Eclipse provides tooling to support creating projects 
with the Splunk SDK for Java.

Splunk is a search engine and analytic environment that uses a distributed
map-reduce architecture to efficiently index, search and process large 
time-varying data sets.

The Splunk product is popular with system administrators for aggregation and
monitoring of IT machine data, security, compliance and a wide variety of other
scenarios that share a requirement to efficiently index, search, analyze and
generate real-time notifications from large volumes of time series data.

The Splunk developer platform enables developers to take advantage of the same
technology used by the Splunk product to build exciting new applications that
are enabled by Splunk's unique capabilities.

## Getting started with the Splunk plug-in for Eclipse

The Splunk plug-in for Eclipse provides extensions to the Eclipse environment
to simplify creating projects using the Splunk SDK for Java. It provides a
new "Splunk SDK for Java" project kind, a set of code templates for common
tasks using the Splunk SDK for Java, and structured editors for Splunk search
commands and time ranges.

The information in this Readme provides steps to get going quickly, but for more
in-depth information be sure to visit the 
[Splunk Developer Portal](http://dev.splunk.com/view/SP-CAAAEBB). 

### Requirements

Here's what you need to get going with the Splunk plug-in for Eclipse.

#### Eclipse

The Splunk plug-in requires Eclipse [TODO: earliest?] to 4.2 (Juno).

#### Splunk

If you haven't already installed Splunk, download it 
[here](http://www.splunk.com/download). For more about installing and running 
Splunk and system requirements, see 
[Installing & Running Splunk](http://dev.splunk.com/view/SP-CAAADRV). 

#### Splunk plug-in for Eclipse

You can install the Splunk plug-in for Eclipse via the Eclipse marketplace.

[TODO: instructions]

### Changelog

The **CHANGELOG.md** file in the root of the repository contains a description
of changes for each version of the SDK. You can also find it online at 
[https://github.com/splunk/splunk-eclipse/blob/master/CHANGELOG.md](https://github.com/splunk/splunk-eclipse/blob/master/CHANGELOG.md).

### Branches

The **master** branch always represents a stable and released version of the SDK.
You can read more about our branching model on our Wiki at 
[https://github.com/splunk/splunk-eclipse/wiki/Branching-Model](https://github.com/splunk/splunk-eclipse/wiki/Branching-Model).

## Documentation and resources
If you need to know more: 

* For all things developer with Splunk, your main resource is the 
  [Splunk Developer Portal](http://dev.splunk.com).

* (TODO: Eclipse specific Splunk docs)

* For more about the Splunk REST API, see the 
  [REST API Reference](http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI).

* For more about about Splunk in general, see 
  [Splunk>Docs](http://docs.splunk.com/Documentation/Splunk).

* For more about this SDK's repository, see our 
  [GitHub Wiki](https://github.com/splunk/splunk-sdk-python/wiki/).

## Community

Stay connected with other developers building on Splunk.

<table>

<tr>
<td><b>Email</b></td>
<td>devinfo@splunk.com</td>
</tr>

<tr>
<td><b>Issues</b>
<td><span>https://github.com/splunk/splunk-eclipse/issues/</span></td>
</tr>

<tr>
<td><b>Answers</b>
<td><span>http://splunk-base.splunk.com/tags/eclipse/</span></td>
</tr>

<tr>
<td><b>Blog</b>
<td><span>http://blogs.splunk.com/dev/</span></td>
</tr>

<tr>
<td><b>Twitter</b>
<td>@splunkdev</td>
</tr>

</table>


### How to contribute

If you would like to contribute to the SDK, go here for more information:

* [Splunk and open source](http://dev.splunk.com/view/opensource/SP-CAAAEDM)

* [Individual contributions](http://dev.splunk.com/goto/individualcontributions)

* [Company contributions](http://dev.splunk.com/view/companycontributions/SP-CAAAEDR)

### Support

1. You will be granted support if you or your company are already covered 
   under an existing maintenance/support agreement. Send an email to 
   _support@splunk.com_ and include "Splunk SDK for Python" in the subject line. 

2. If you are not covered under an existing maintenance/support agreement, you 
   can find help through the broader community at:

   <ul>
   <li><a href='http://splunk-base.splunk.com/answers/'>Splunk Answers</a> (use 
    the <b>sdk</b>, <b>java</b>, <b>python</b>, and <b>javascript</b> tags to 
    identify your questions)</li>
   <li><a href='http://groups.google.com/group/splunkdev'>Splunkdev Google 
    Group</a></li>
   </ul>
3. Splunk will NOT provide support for SDKs if the core library (the 
   code in the <b>/splunklib</b> directory) has been modified. If you modify an 
   SDK and want support, you can find help through the broader community and 
   Splunk answers (see above). We would also like to know why you modified the 
   core library&mdash;please send feedback to _devinfo@splunk.com_.
4. File any issues on 
   [GitHub](https://github.com/splunk/splunk-sdk-python/issues).

### Contact Us

You can reach the Developer Platform team at _devinfo@splunk.com_.

## License

The Splunk Software Development Kit for Python is licensed under the Apache
License 2.0. Details can be found in the file LICENSE.

For compatibility with Python 2.6, The Splunk Software Development Kit
for Python ships with ordereddict.py from the ordereddict package on
[PyPI](http://pypi.python.org/pypi/ordereddict/1.1), which is licensed
under the MIT license (see the top of splunklib/ordereddict.py).
