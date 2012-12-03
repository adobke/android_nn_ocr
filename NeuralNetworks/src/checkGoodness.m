function [ output ] = checkGoodness( net, inputs, answers )
    output = sum(sum(compet(sim(net,inputs)) ~= answers))/2;
end

