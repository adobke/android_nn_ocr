function [ net, errors ] = trainNet( inputs, outputs )
% Mark Mann and Alistair Dobke
% CS152 Fall 2012
% Final project - OCR math
net = feedforwardnet([65 58]);

% Change the maximum number of runs that will occur
% before the net determines that the problem is intractable.
net.trainParam.epochs = 1000;

net.layers{1}.transferFcn = 'logsig';
net.layers{2}.transferFcn = 'logsig';

% Set the training function to scaled conjugate gradient.
net.trainFcn = 'trainscg';
% Need this setting when using trainlm
% net.efficiency.memoryReduction = 2;

% Configure the network.
net = configure(net, inputs, outputs);
net = init(net);

% Set the number of items to use to train.
net.divideParam.trainRatio = 100/100;

% Set the number of times to continue to try to improve the system before
% quiting.
net.trainParam.max_fail = 20;

net = train(net, inputs, outputs);

% This is how you get the weights of the neural net.
%      inputW = net.IW{1,1};
%      firstLayerW = net.LW{2,1};
%      secondLayerW = net.LW{3,2};

% How well did we do?
errors = sum(sum(compet(sim(net, inputs)) ~= outputs))/2;
percError = 100 - (errors/size(outputs,2)*100);
fprintf('%f percent success\n', percError)

end

