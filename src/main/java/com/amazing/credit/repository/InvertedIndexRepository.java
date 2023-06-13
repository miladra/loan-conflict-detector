package com.amazing.credit.repository;

import com.amazing.credit.model.InvertedIndex;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class InvertedIndexRepository {

    private final Map<String , List<InvertedIndex>> indices = new ConcurrentHashMap<>();

    public void add(String hashKey , String fileName , int index , BigDecimal creditLimit){
        if(indices.containsKey(hashKey)){
            //  If there is already an index with same hash key, check if there
            //  is same credit limit
            List<InvertedIndex> indicesOfCurrentKey = indices.get(hashKey);
            List<InvertedIndex> conflictCredit = indicesOfCurrentKey.stream().filter(i-> !i.getCreditLimit().equals(creditLimit)).collect(Collectors.toList());
            if(conflictCredit.size() > 0){

                // Update other credit
                conflictCredit.forEach(t-> t.setConflict(true));

                //  If there is conflict credit limit, add new index
                indicesOfCurrentKey.add(new InvertedIndex(fileName, index, creditLimit , true));


            } else {
                indicesOfCurrentKey.add(new InvertedIndex(fileName, index, creditLimit , false));
            }
        } else {
            List<InvertedIndex> newInvertedIndices = new ArrayList<>();
            newInvertedIndices.add(new InvertedIndex(fileName , index , creditLimit, false));
            indices.put(hashKey , newInvertedIndices);
        }
    }

    public List<InvertedIndex> getIndices(String hashKey){
        return indices.get(hashKey);
    }

    public int size(){
        return indices.size();
    }
    
    // For testing purposes
    public void clear(){
        indices.clear();
    }

    public Stream<List<InvertedIndex>> getIndicesStream() {
        return indices.values().stream();
    }
}
