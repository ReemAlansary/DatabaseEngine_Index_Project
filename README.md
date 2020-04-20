# Database Engine with BPtrees and RTrees Indices Support
### File Structure
- :open_file_folder: __CodingRaptors__
   - :open_file_folder: __config__
     - [DBApp.properties](config/DBApp.properties)
   - :open_file_folder: __data__
     - [metadata.csv](data/metadata.csv)
   - :open_file_folder: __docs__
   - :open_file_folder: __src__
     - :open_file_folder: __CodingRaptors__
       - [CPolygon.java](src/CodingRaptors/CPolygon.java)
       - [DBApp.java](src/CodingRaptors/DBApp.java)
       - [DBAppException.java](src/CodingRaptors/DBAppException.java)
       - [DBAppTest.java](src/CodingRaptors/DBAppTest.java)
       - [Page.java](src/CodingRaptors/Page.java)
       - [SQLTerm.java](src/CodingRaptors/SQLTerm.java)
     - :open_file_folder: __Indices__
       - [BPTree.java](src/Indices/BPTree.java)
       - [Leaf.java](src/Indices/Leaf.java)
       - [Node.java](src/Indices/Node.java)
       - [NodeEntry.java](src/Indices/NodeEntry.java)
       - [NonLeaf.java](src/Indices/NonLeaf.java)
       - [OverFlowPage.java](src/Indices/OverFlowPage.java)
       - [Pointer.java](src/Indices/Pointer.java)
       - [RTree.java](src/Indices/RTree.java)
       - [TupleExtraPointer.java](src/Indices/TupleExtraPointer.java)
       - [TuplePointer.java](src/Indices/TuplePointer.java)

#### General Description of What the DBEngine Supports
- Metadata about each table [Table name, Column names, Column Types, Clustering Key, Indexed Columns] are saved in the `metadata.csv file`.
- Data for each table are inserted into serialized pages in the `data/ folder` which can store a pre-defined number of tuple according to the `DBApp.properties` file.
- Data for each table is sorted by the **clustering key**.

#### General Description of What the BPtree Index Supports
- BPTree created on a table column saves the **`values`** for this column in its leaves.
- The BPTree and nodes [Non-leaf, Leaf] are serialized in the `data/ folder`.
- Duplicates for a certain value are stored in overflow pages which are also serialized in the `data/ folder`.

#### General Description of What the Rtree Index Supports
- RTree created on a table column saves the **`areas`** for this column polygons in its leaves.
   - However, in update and delete methods, the search is done using the coordinates as well as the area.
- The RTree and nodes [Non-leaf, Leaf] are serialized in the `data/ folder`.
- Duplicates for a certain value are stored in overflow pages which are also serialized in the `data/ folder`.

#### Insertion
- Any tuple is inserted in its correct place according to the ordering of the clustering key.
- **Lazy shifting** is utilized, where any empty space in prior pages is used so that tuples can be shifted upwards between several pages if there is no space in the current page.
- Binary search is used.

#### Update
- The WHERE clause contains a condition for the clustering key only.
- If the clustering key is indexed, its index is used for faster search for the tuples to be updated.
- If the clustering key is not indexed, `binary search` is used.

#### Delete
- The WHERE clause contains a condition for any column.
- If any of those columns are indexed, its index is used for faster search for the tuples to be deleted.
- No shifting is done between pages.
- If no column is indexed, `linear search` is used.

#### SELECT Queries
- No joins are supported.
- The filtering of tuples to be selected is optimized.
