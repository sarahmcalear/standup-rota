require 'json'

json = File.read('rotation')

$people = JSON.parse(json)

puts 'Current rotation status:'
puts $people

puts 'How many weeks to plan?'
num = gets().to_i
num.times do |week|
  list = $people.sort_by { | name, times| times}.reverse
  pair = list.take(2).map { |x| x[0] }
  puts "#{week}: #{pair[0]} #{pair[1]}"

  pair.each { | person | $people[person] = 0 }

  list.drop(2).map { |x| x[0] }.each { |person| $people[person] = $people[person] + rand(2) + 1}
end
puts $people
File.open('rotation','w') do |f|
  f.write($people.to_json)
end
